package ftn.bsep9.service;

import ftn.bsep9.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;


public interface LogsService {

    Boolean findByText(String text, int page, Model model);

    Boolean findByDate(String text, Model model);

    Boolean findAllWithPages(Model model, int pageStart, int pageSize, Sort.Direction sortDirection, String sortField);

}
