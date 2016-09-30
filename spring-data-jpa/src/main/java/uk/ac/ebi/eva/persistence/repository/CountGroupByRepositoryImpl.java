package uk.ac.ebi.eva.persistence.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by jorizci on 29/09/16.
 */
public class CountGroupByRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CountGroupByRepository<T, ID> {

    private EntityManager entityManager;

    public CountGroupByRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public CountGroupByRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<Tuple> groupCount(String columnName){
        return groupCount(columnName,null);
    }

    @Override
    public List<Tuple> groupCount(String columnName, Specification<T> specification) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);

        Root<T> root = criteriaQuery.from(getDomainClass());
        Path columnNamePath = root.get(columnName);

        Expression countExpression = criteriaBuilder.count(columnNamePath);
        criteriaQuery.select(criteriaBuilder.tuple(columnNamePath, countExpression));
        criteriaQuery.groupBy(columnNamePath);

        if(specification!=null) {
            criteriaQuery.where(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
        }

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }
}
