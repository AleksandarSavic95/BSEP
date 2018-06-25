package ftn.bsep9.service;

import ftn.bsep9.model.AlarmFile;
import org.springframework.data.domain.Page;

public interface AlarmFileService {
    AlarmFile create(AlarmFile alarmFile);

    AlarmFile get(String alarmName);

    AlarmFile update(String alarmName, AlarmFile updatedFile);

    boolean delete(String alarmName);

    Page<String> findAllWithPages(int page, int size, String sortDirection);
}
