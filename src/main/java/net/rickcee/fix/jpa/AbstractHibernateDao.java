/**
 * 
 */
package net.rickcee.fix.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author rickcee
 *
 */
@Transactional
public abstract class AbstractHibernateDao<T extends Serializable> {
	private Class<T> clazz;

	@Autowired
	//SessionFactory sessionFactory;
	private EntityManager em;

	public void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public T findOne(long id) {
		return em.find(clazz, id);
	}

	public List<T> findAll() {
		//return getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
		return em.createQuery("from " + clazz.getName(), clazz).getResultList();
	}

	public T create(T entity) {
		return em.merge(entity);
	}

	@SuppressWarnings("unchecked")
	public T update(T entity) {
		return em.merge(entity);
	}

	public void delete(T entity) {
		em.remove(entity);
	}

	public void deleteById(long entityId) {
		T entity = findOne(entityId);
		delete(entity);

	}
}
