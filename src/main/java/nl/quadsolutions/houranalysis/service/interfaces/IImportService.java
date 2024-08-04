package nl.quadsolutions.houranalysis.service.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface IImportService {

    void importData(MultipartFile file) throws IOException;
}
