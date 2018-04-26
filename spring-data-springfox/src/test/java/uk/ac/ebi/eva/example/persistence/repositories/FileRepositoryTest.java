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
package uk.ac.ebi.eva.example.persistence.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.eva.example.persistence.entities.File;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaAuditing
public class FileRepositoryTest {

    @Autowired
    private FileRepository repository;

    @Test
    public void testSave() {
        assertEquals(0, repository.count());
        File file = repository.save(new File("test-file", File.Type.BAM, 100L));
        assertEquals(1, repository.count());
        assertTrue(file.getId() != null);
    }

    @Test
    public void testFindType() {
        assertEquals(0, repository.count());
        File file = repository.save(new File("test-file", File.Type.BAM, 100L));
        assertEquals(1, repository.count());
        assertTrue(file.getId() != null);

        assertEquals(file.getId(), repository.findByType(File.Type.BAM).getId());
        assertNull(repository.findByType(File.Type.CRAM));
    }

    @Test
    public void testFindAllType() {
        assertEquals(0, repository.count());
        repository.save(new File("test-file", File.Type.BAM, 100L));
        repository.save(new File("test-file2", File.Type.BAM, 100L));
        repository.save(new File("test-file3", File.Type.CRAM, 100L));
        repository.save(new File("test-file4", File.Type.BAM, 100L));
        assertEquals(4, repository.count());

        assertEquals(3, repository.findAllByType(File.Type.BAM).size());
        assertEquals(1, repository.findAllByType(File.Type.CRAM).size());
    }

    @Test
    public void testAuditing() throws InterruptedException {
        assertEquals(0, repository.count());
        File fileTime1 = repository.save(new File("test-file", File.Type.BAM, 100L));
        LocalDateTime created1 = fileTime1.getCreatedDate();
        LocalDateTime updated1 = fileTime1.getLastModifiedDate();
        System.out.println("creation: " + created1 + " update: " + updated1);

        Thread.sleep(10);

        fileTime1.setName("test-file-updated");
        repository.save(fileTime1);
        // Force entity manager flush
        assertEquals(1, repository.count());

        File fileTime2 = repository.findOne(fileTime1.getId());
        LocalDateTime created2 = fileTime2.getCreatedDate();
        LocalDateTime updated2 = fileTime2.getLastModifiedDate();

        System.out.println("creation: " + created2 + " update: " + updated2);

        assertEquals(created1, created2);
        assertNotSame(updated1, updated2);
    }



}
