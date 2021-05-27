package dao;

import model.Pessoa;

public class DaoPessoa <E> extends DaoGeneric<Pessoa>{
	
	public void removerUsuario(Pessoa pessoa) throws Exception {
		
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
	

}
