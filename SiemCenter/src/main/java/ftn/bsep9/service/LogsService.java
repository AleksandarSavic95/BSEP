package ftn.bsep9.service;

import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;


public interface LogsService {

    Boolean findByText(String text, Integer page, Model model);

//    Boolean findByDate(String text, Model model);

    Boolean findAllWithPages(Model model, Integer pageStart, Integer pageSize, Sort.Direction sortDirection, String sortField);

}
