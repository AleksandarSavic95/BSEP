package ftn.bsep9.service.serviceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import ftn.bsep9.utility.SearchParser;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@Service
public class LogsServiceImpl implements LogsService {

    @Autowired
    LogsRepository logsRepository;

    private final KieSession kieSession;

    @Autowired
    public LogsServiceImpl(KieSession kieSession) {
        System.out.println("\t\tInitializing a KIE Session...");
        this.kieSession = kieSession;
    }

    public Page<Log> findAllWithPages(Integer page, Integer size,
                                      Sort.Direction sortDirection, String sortField) {
        Page<Log> logs = null;
        try {
            Long count = this.logsRepository.count();
            page = checkPageRange(page, count / size);

            PageRequest pageRequest = new PageRequest(page, size,
                    new Sort(sortDirection, sortField));

            logs = this.logsRepository.findAll(pageRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

        return logs;
    }


    @Override
    public Object findByText(String text, Integer page, Integer size) {

        Page<Log> logs;
        QLog qLog = new QLog("logs");  // create a query class (QLog)

        try {
            BooleanExpression finalBooleanExpression = SearchParser.parse(text, qLog);

            Long count = this.logsRepository.count(finalBooleanExpression);
            page = checkPageRange(page, count / size);

            logs = this.logsRepository.findAll(
                    finalBooleanExpression, new PageRequest(page, size)); // filterByDate
             //, Sort.Direction.ASC, "MACAddress"
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return logs;
    }


    private Integer checkPageRange(Integer page, Long totalPages) {
        if (page < 0 || page > totalPages) {
            page = 0;
        }

        return page;
    }

    @Override
    public void saveLog(Log log) {
        kieSession.insert(log);
        // fire rules without defined "agenda-group" attribute
        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        kieSession.fireAllRules();
    }
}

