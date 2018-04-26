/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.eva.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.eva.example.persistence.entities.File;
import uk.ac.ebi.eva.example.persistence.repositories.FileRepository;

@Component
public class DataLoadConfiguration {

    private FileRepository fileRepository;

    @Autowired
    public DataLoadConfiguration(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        loadFiles();
    }

    private void loadFiles() {
        fileRepository.save(new File("test-file1", File.Type.BAM, 100L));
        fileRepository.save(new File("test-file2", File.Type.VCF, 120L));
        fileRepository.save(new File("test-file3", File.Type.CRAM, 150L));
        fileRepository.save(new File("test-file4", File.Type.BAM, 200L));
        fileRepository.save(new File("test-file5", File.Type.VCF, 420L));
        fileRepository.save(new File("test-file6", File.Type.CRAM, 350L));
        fileRepository.save(new File("test-file7", File.Type.BAM, 600L));
        fileRepository.save(new File("test-file8", File.Type.BINARY, 420L));
        fileRepository.save(new File("test-file9", File.Type.COMPRESSED_VCF, 350L));
        fileRepository.save(new File("test-file10", File.Type.BINARY, 600L));
        fileRepository.save(new File("test-file11", File.Type.BAM, 1000L));
        fileRepository.save(new File("test-file12", File.Type.VCF, 1200L));
        fileRepository.save(new File("test-file13", File.Type.CRAM, 1500L));
        fileRepository.save(new File("test-file14", File.Type.BAM, 2000L));
        fileRepository.save(new File("test-file15", File.Type.VCF, 4200L));
        fileRepository.save(new File("test-file16", File.Type.CRAM, 3500L));
        fileRepository.save(new File("test-file17", File.Type.BAM, 6000L));
        fileRepository.save(new File("test-file18", File.Type.BINARY, 4020L));
        fileRepository.save(new File("test-file19", File.Type.COMPRESSED_VCF, 3050L));
        fileRepository.save(new File("test-file20", File.Type.BINARY, 6000L));
        fileRepository.save(new File("test-file21", File.Type.BAM, 100L));
        fileRepository.save(new File("test-file22", File.Type.VCF, 120L));
        fileRepository.save(new File("test-file23", File.Type.CRAM, 150L));
        fileRepository.save(new File("test-file24", File.Type.BAM, 200L));
        fileRepository.save(new File("test-file25", File.Type.VCF, 420L));
        fileRepository.save(new File("test-file26", File.Type.CRAM, 350L));
        fileRepository.save(new File("test-file27", File.Type.BAM, 600L));
        fileRepository.save(new File("test-file28", File.Type.BINARY, 420L));
        fileRepository.save(new File("test-file29", File.Type.COMPRESSED_VCF, 350L));
        fileRepository.save(new File("test-file30", File.Type.BINARY, 600L));
    }

}
