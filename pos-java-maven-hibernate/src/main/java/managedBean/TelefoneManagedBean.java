package managedBean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dao.DaoTelefones;
import dao.DaoUsuario;
import model.TelefoneUser;
import model.UsuarioPessoa;

@ManagedBean(name = "telefoneManagedBean")
@ViewScoped
public class TelefoneManagedBean {
	
	private UsuarioPessoa user = new UsuarioPessoa();
	private DaoUsuario<UsuarioPessoa> daoUer = new DaoUsuario<UsuarioPessoa>(); 
	private DaoTelefones<TelefoneUser> daoTelefone = new DaoTelefones<TelefoneUser>();
	private TelefoneUser foneUser = new TelefoneUser();
	
	private List<TelefoneUser> list= new ArrayList<TelefoneUser>();
	
	
	
	
	@PostConstruct
	public void init() {
		
		String coduser = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("codigouser");//get o nome do parametro na tela index.jsf
		user = daoUer.pesquisar(Long.parseLong(coduser), UsuarioPessoa.class);
						
	}
	
	
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);		
	}
	
	public String salvar() {
		foneUser.setUsuarioPessoa(user); //usar com referencia ao usuario pq a tabela telefone depende de usuarioPessoa
		daoTelefone.salvar(foneUser);
		user = daoUer.pesquisar(user.getId(), UsuarioPessoa.class); //para atualizar a tela e os dados na tabela apos a ação 
		foneUser = new TelefoneUser();
		mostrarMsg("cadastrado com sucesso!");		
		return "";
	}
	
	
	
	public String remover() throws Exception {	
		
		daoTelefone.deletarPorId(foneUser);
		user = daoUer.pesquisar(user.getId(), UsuarioPessoa.class); //para atualizar a tela e os dados na tabela apos a ação 
		foneUser = new TelefoneUser();
		mostrarMsg("removido com sucesso!");
		
		return "";
	}	





	
	
	
	
	
	
	
	
	
	
	
	
	//----------------------------------
	
	public UsuarioPessoa getUser() {
		return user;
	}


	public void setUser(UsuarioPessoa user) {
		this.user = user;
	}
	
	
	public DaoTelefones<TelefoneUser> getDaoTelefone() {
		return daoTelefone;
	}
	
	public void setDaoTelefone(DaoTelefones<TelefoneUser> daoTelefone) {
		this.daoTelefone = daoTelefone;
	}
	
	public TelefoneUser getFoneUser() {
		return foneUser;
	}
	
	public void setFoneUser(TelefoneUser foneUser) {
		this.foneUser = foneUser;
	}

	public List<TelefoneUser> getList() {
		list = daoTelefone.listar(TelefoneUser.class);
		return list;
	}


	

}
