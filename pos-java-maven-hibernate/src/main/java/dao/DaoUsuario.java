package dao;

import java.util.List;

import javax.persistence.Query;

import model.UsuarioPessoa;

public class DaoUsuario <E> extends DaoGeneric<UsuarioPessoa>{
	
	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		
		getEntityManager().getTransaction().begin();
		getEntityManager().remove(pessoa);			
		getEntityManager().getTransaction().commit();
		
		/* METODO SEM CASCADE REMOVE
		String sqlDeleteFone = "delete from TelefoneUser where usuariopessoa_id = " + pessoa.getId();
		String sqlDeleteEmail = "delete from emailuser where usuariopessoa_id = " + pessoa.getId();
		
		
		getEntityManager().getTransaction().begin();		
		getEntityManager().createNativeQuery(sqlDeleteFone).executeUpdate();
		getEntityManager().createNativeQuery(sqlDeleteEmail).executeUpdate();
		
		getEntityManager().getTransaction().commit();
		
		super.deletarPorId(pessoa);
		*/
		
		
		
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioPessoa> consultarUsuario(String campoPesquisa) {
		
		Query query = super.getEntityManager().createQuery("from UsuarioPessoa where lower(nome) like '%"+campoPesquisa+"'");
				
		return query.getResultList();
	}
	

}
