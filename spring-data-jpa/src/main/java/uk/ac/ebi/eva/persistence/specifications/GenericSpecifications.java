package uk.ac.ebi.eva.persistence.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by jorizci on 30/09/16.
 */
public class GenericSpecifications<T> {

    public static <T> Specification<T> isEqual(String attributeName, Object object) {
        return new Specification<T>(){

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(attributeName),object);
            }
        };
    }

}
