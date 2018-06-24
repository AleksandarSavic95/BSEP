package ftn.bsep9.service;

import ftn.bsep9.model.AlarmFile;
import org.springframework.beans.support.PagedListHolder;

public interface AlarmStorageService {
    AlarmFile create(AlarmFile alarmFile);

    AlarmFile get(String alarmName);

    AlarmFile update(String alarmName, AlarmFile updatedFile);

    boolean delete(String alarmName);

    PagedListHolder<String> findAllWithPages(int page, int size, String sortDirection);
}
