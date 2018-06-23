package ftn.bsep9.service;

import ftn.bsep9.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;


public interface LogsService {

    Object findByText(String text, Integer page, Integer size);

//    Boolean findByDate(String text, Model model);

    Page<Log> findAllWithPages(Integer pageStart, Integer pageSize, Sort.Direction sortDirection, String sortField);

    /**
     * Inserts the log into the KIE-session (LATER: and into the database)
     * @param log log to be saved.
     */
    void saveLog(Log log);
}
