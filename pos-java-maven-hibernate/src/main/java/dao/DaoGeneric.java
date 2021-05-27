package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import posjavamavenhibernate.HibernateUtil;

public class DaoGeneric<E> {

	private EntityManager entityManager = HibernateUtil.getEntityManager();

	public void salvar(E entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(entidade);
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public E pesquisar(E entidade) {

		Object id = HibernateUtil.getPrimaryKey(entidade);

		E e = (E) entityManager.find(entidade.getClass(), id);

		return e;

	}

	public E pesquisar(Long id, Class<E> entidade) {
		
		entityManager.clear(); //limpar dados em memoria

		E e = (E) entityManager.createQuery("from " + entidade.getSimpleName() + " where id = " + id).getSingleResult();

		return e;

	}

	public E updateMerge(E entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		E entidadeSalva = entityManager.merge(entidade);
		transaction.commit();

		return entidadeSalva;
	}
	
	
	public void removerUsuarioTeste(E entidade) throws Exception {
		
		getEntityManager().getTransaction().begin();
		getEntityManager().remove(entidade);			
		getEntityManager().getTransaction().commit();

		
	}
	

	public void deletarPorId(E entidade) throws Exception {
		
		Object id = HibernateUtil.getPrimaryKey(entidade); //obtem o id do objeto PK
		
		EntityTransaction transaction = entityManager.getTransaction(); //objeto de transação
		transaction.begin(); //começa uma transaçao no banco de dados
		
		entityManager.createNativeQuery(//monta query para delete
				"delete from " + entidade.getClass().getSimpleName().toLowerCase() + 
				" where id =" + id).executeUpdate();
		transaction.commit(); //grava alteracao no banco dados		
	}
	
	@SuppressWarnings("unchecked")
	public List<E> listar(Class<E> entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<E> lista = entityManager.createQuery("from " + entidade.getName()).getResultList();
		
		transaction.commit();
		
		return lista;
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<E> getListEntity(Class<E> entidade){
		EntityManager entityManager = HibernateUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		List<E> retorno = entityManager.createQuery("from " + entidade.getName()).getResultList();
		
		entityTransaction.commit();
		entityManager.close();		
		
		return retorno;
		
	}
	
	

}
