package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.repository.AlarmFilesRepository;
import ftn.bsep9.service.AlarmFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

@Service
public class AlarmFileServiceImpl implements AlarmFileService {

    @Autowired
    private AlarmFilesRepository alarmFilesRepository;

    @Override
    public AlarmFile create(AlarmFile alarmFile) {
        return alarmFilesRepository.saveAlarm(alarmFile, false);
    }

    @Override
    public AlarmFile get(String alarmName) {
        File file = alarmFilesRepository.getRuleFile(alarmName);
        if (!file.exists()) {
            return null;
        }
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(encoded, StandardCharsets.UTF_8);
        return new AlarmFile(alarmName, content);
    }

    @Override
    public AlarmFile update(String alarmName, AlarmFile updatedFile) {
        if (!updatedFile.getName().equals(alarmName)) { // sanity check
            return null;
        }
        if (!alarmFilesRepository.fileExists(alarmName))
            return null;
        return alarmFilesRepository.saveAlarm(updatedFile, true);
    }

    @Override
    public boolean delete(String alarmName) {
        return alarmFilesRepository.delete(alarmName);
    }

    @Override
    public Page<String> findAllWithPages(int page, int size, String sortDirection) {

        // TODO: what happens after we make endpoint/folder for siem's rules??
        List<String> fileNames = alarmFilesRepository.getFileNamesAscending();

        if (! sortDirection.equals("asc")) { // ascending is default
            Collections.reverse(fileNames);
        }

//        PagedListHolder<String> pagedListHolder = new PagedListHolder<>(fileNames);
//        pagedListHolder.setPage(page);
//        pagedListHolder.setPageSize(size);
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(fileNames, pageable, fileNames.size());
    }
}
