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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import uk.ac.ebi.eva.example.persistence.entities.File;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FileRepositoryImpl implements FileRepositoryExtension {

    private final static Logger logger = LoggerFactory.getLogger(FileRepositoryImpl.class);

    private JpaEntityInformation<File, ?> entityInformation;

    private EntityManager entityManager;

    public FileRepositoryImpl(EntityManager entityManager) {
        entityInformation = JpaEntityInformationSupport.getEntityInformation(File.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public void setType(long id, File.Type type) {
        String entity = entityInformation.getEntityName();
        Query query = entityManager.createQuery(
                "UPDATE " + entity + " SET type=:fileType WHERE id = :entityId ");
        query.setParameter("fileType", type);
        query.setParameter("entityId", id);
        logger.info(Integer.toString(query.executeUpdate()));
        entityManager.clear();
    }

}
