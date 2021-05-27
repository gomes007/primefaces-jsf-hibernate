package datatablelazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import dao.DaoUsuario;
import model.UsuarioPessoa;

public class LazyDataTableModelUserPessoa<T> extends LazyDataModel<UsuarioPessoa> {
	

	private static final long serialVersionUID = 1L;

	private DaoUsuario<UsuarioPessoa> dao = new DaoUsuario<UsuarioPessoa>();
	
	public List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	
	private String sql = " from UsuarioPessoa ";

	@SuppressWarnings("unchecked")
	@Override
	public List<UsuarioPessoa> load(int first, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filterBy) {
		
		list = dao.getEntityManager().createQuery(getSql()).setFirstResult(first).setMaxResults(pageSize).getResultList();
		
		sql = " from UsuarioPessoa "; //repete aqui o sql para recomecar mesmo apos a consultado campo pesquisar na tela
		
		setPageSize(pageSize);
		
		Integer qdeRegistro = Integer.parseInt(dao.getEntityManager().createQuery(" select count(1) " + getSql()).getSingleResult().toString());
		setRowCount(qdeRegistro);
		
		return list;
	}
	
	public void pesquisar(String campoPesquisa) {
		sql += " where nome like '%"+campoPesquisa+"%'";
	}
	
	
	
	public String getSql() {
		return sql;
	}
	
	public List<UsuarioPessoa> getList() {
		return list;
	}
	
	

}
