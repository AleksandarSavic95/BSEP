package ftn.bsep9.service.serviceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import ftn.bsep9.utility.SearchParser;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public Boolean findAllWithPages(Model model, Integer page, Integer size,
                                      Sort.Direction sortDirection, String sortField) {

        try {
            Long count = this.logsRepository.count();
            page = checkPageRange(page, count / size, model);

            PageRequest pageRequest = new PageRequest(page, size,
                    new Sort(sortDirection, sortField));

            Page<Log> logs = this.logsRepository.findAll(pageRequest);

            model.addAttribute("logs", logs);
            model.addAttribute("totalPages", logs.getTotalPages());
            model.addAttribute("currentPage", page);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("title", "Bad Request");
            return false;
        }

        return true;
    }


    @Override
    public Boolean findByText(String text, Integer page, Model model) {

        Integer size = 3;
        Page<Log> logs;
        QLog qLog = new QLog("logs");  // create a query class (QLog)

        text = text.substring(5);  // uklonimo "text="
        text = text.replace("+", " ");
        text = text.replace("%2B", "+");
        text = text.replace("%3A", ":");
        text = text.replace("%3B", ";");
        text = text.replace("%2C", ",");
        text = text.replace("%26", "&");
        text = text.replace("%28", "(");
        text = text.replace("%29", ")");
        text = text.replace("%5B", "[");
        text = text.replace("%5D", "]");
        text = text.replace("%7B", "{");
        text = text.replace("%7D", "}");
        text = text.replace("%22", "\"");
        text = text.replace("%24", "$");
        text = text.replace("%3D", "=");
        text = text.replace("%5C", "\\");
        text = text.replace("%2F", "/");

        model.addAttribute("searchString", text);  // dodamo pretragu po tekstu u promjenljivu za sljedecu stranicu

        try {
            BooleanExpression finalBooleanExpression = SearchParser.parse(text, qLog);

            Long count = this.logsRepository.count(finalBooleanExpression);
            page = checkPageRange(page, count / size, model);

            logs = this.logsRepository.findAll(
                    finalBooleanExpression, new PageRequest(page, size)); // filterByDate
             //, Sort.Direction.ASC, "MACAddress"

            model.addAttribute("logs", logs);
            model.addAttribute("totalPages", logs.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("searchedString", text);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("title", "Bad Request");
            return false;
        }

        return true;
    }


    private Integer checkPageRange(Integer page, Long totalPages, Model model) {
        if (page < 0 || page > totalPages) {
            page = 0;
            model.addAttribute("pageOutOfRange", true);
        }

        return page;
    }

    @Override
    public void saveLog(Log log) {
        kieSession.insert(log);
        kieSession.fireAllRules();
    }
}

