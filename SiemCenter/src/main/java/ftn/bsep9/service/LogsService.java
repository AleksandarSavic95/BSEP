package ftn.bsep9.service;

import ftn.bsep9.model.Log;
import org.springframework.ui.Model;

import java.util.List;


public interface LogsService {

    Boolean findByText(String text, Model model);

    Boolean findByDate(String text, Model model);
}
