package managedBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.google.gson.Gson;

import dao.DaoEmail;
import dao.DaoUsuario;
import datatablelazy.LazyDataTableModelUserPessoa;
import model.EmailUser;
import model.UsuarioPessoa;

@ManagedBean(name = "usuarioPessoaManagedBean")
@ViewScoped
public class UsuarioPessoaManagedBean {
	
	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	/*private DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<>();*/
	private LazyDataTableModelUserPessoa<UsuarioPessoa> list = new LazyDataTableModelUserPessoa<UsuarioPessoa>();
	private DaoUsuario<UsuarioPessoa> daoGeneric = new DaoUsuario<UsuarioPessoa>();
	private EmailUser emailUser = new EmailUser();
	private DaoEmail<EmailUser> daoEmail = new DaoEmail<EmailUser>();
	
	private String campoPesquisa;
	
	
	private BarChartModel barChartModel = new BarChartModel();
	
	
	
	//para tratar excessao com violacao chave primaria na hora de apagar registro
	@PostConstruct
	private void init() {
		
		list.load(0, 5, null, null);
		montarGrafico();
		
	}


	private void montarGrafico() {
		
		barChartModel = new BarChartModel(); //para atualizar a tela do grafico apos salvar ou atualizar
		
		//criar grafico
		ChartSeries userSalario = new ChartSeries(); //cria o grupo de funcionarios
		
		for (UsuarioPessoa usuarioPessoa : list.list) { //adiciona salario e nome para o grupo e tem 2 list.list devido ao lazydata
			userSalario.set(usuarioPessoa.getNome(), usuarioPessoa.getSalario()); //adiciona salarios
		}
		
		barChartModel.addSeries(userSalario); //adiciona o grupo
		barChartModel.setTitle("grafico de salarios");
	}
		
	
	public BarChartModel getBarChartModel() {
		return barChartModel;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------
	
	public String salvar() {
		daoGeneric.salvar(usuarioPessoa);
		list.list.add(usuarioPessoa); //uma lista da classe lazydata e outra list = new LazyDataTableModelUserPessoa<UsuarioPessoa>();
		init();
		novo();
		return "";
	}
	
	public void addEmail() {
		emailUser.setUsuarioPessoa(usuarioPessoa); //vincula o usuario acionado
		emailUser = daoEmail.updateMerge(emailUser); //salva ou atualiza no banco e volta mesma tela
		usuarioPessoa.getEmails().add(emailUser); //adiciona para a lista de email do usuario
		emailUser = new EmailUser();
		mostrarMsg("cadastrado com sucesso!");
	}
	
	public void removerEmail() throws Exception {
		String codigoemail = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("codigoemail"); //para pegar o parametro
		
		EmailUser removerId = new EmailUser(); //instanciar para ter acesso ao Id
		removerId.setId(Long.parseLong(codigoemail)); //faz o campo id receber o id que vem do parametro convertendo para long
		daoEmail.deletarPorId(removerId); //finalmente passa o id a ser removido  para o metodo deletar por id para apagar do banco de dados
		usuarioPessoa.getEmails().remove(removerId); //finalmente remove da lista de emails do usuario que esta na tela
		init();
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Resultado:", "registro apagado!"));		
	}
	
	
	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		return "";
	}
	
	public String atualizar() {
		daoGeneric.updateMerge(usuarioPessoa);
		init();
		usuarioPessoa = new UsuarioPessoa();
		mostrarMsg("atualizado com sucesso!");
		return "";
	}
	
	


	
	public String remover() {
		try {
			daoGeneric.removerUsuario(usuarioPessoa);//metodo criado para apagar registros com telefones cadastrados na tabela telefoneuser e nao violar pk
			list.list.remove(usuarioPessoa); //para tratar excessao com violacao chave primaria na hora de apagar registro
			usuarioPessoa = new UsuarioPessoa();
			mostrarMsg("apagado com sucesso!");			
		} catch (Exception e) { //para tratar excessao com violacao chave primaria na hora de apagar registro
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				mostrarMsg("usuario com telefone/Email cadastrado!");
			} else {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);		
	}
	
	
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		
		try {
			
			URL url = new URL("https://viacep.com.br/ws/"+usuarioPessoa.getCep()+"/json/");			
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = ""; //variavel para armazenar os dados da api que vem do site
			StringBuilder jsonCep = new StringBuilder();
			
			while ((cep = br.readLine()) != null) {// varrer os campos e usa o stringbuilder para concatenar as informações
				jsonCep.append(cep);
			}
			
			UsuarioPessoa gsonAux = new Gson().fromJson(jsonCep.toString(), UsuarioPessoa.class); //vincula os campos json do web service com os atributos/campos da classe pessoa
			
			usuarioPessoa.setCep(gsonAux.getCep());
			usuarioPessoa.setLogradouro(gsonAux.getLogradouro());
			usuarioPessoa.setBairro(gsonAux.getBairro());
			usuarioPessoa.setComplemento(gsonAux.getComplemento());
			usuarioPessoa.setLocalidade(gsonAux.getLocalidade());
			usuarioPessoa.setUf(gsonAux.getUf());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("erro ao consultar o cep");
		}
		
		
	}
	
	
	//metodo para pesquisar pelo nome
	public void pesquisar() {
		list.pesquisar(campoPesquisa);
		montarGrafico();
	}
	
	
	public void upload(FileUploadEvent image) {
		String imagem = "data:image/png;base64," + DatatypeConverter.printBase64Binary(image.getFile().getContent());
		usuarioPessoa.setImagem(imagem);
	}
	
	
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String fileDownloadID = params.get("fileDownloadId");
		
		UsuarioPessoa pessoa = daoGeneric.pesquisar(Long.parseLong(fileDownloadID), UsuarioPessoa.class);
		
		byte[] imagem = new Base64().decode(pessoa.getImagem().split("\\,")[1]);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename=download.png");		
		response.setContentType("application/octet-stream");
		response.setContentLength(imagem.length);
		response.getOutputStream().write(imagem);
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
			
	}
	
	
	//-------------------------------------------------------------------------
	
	
	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}
	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}
	
	
	public LazyDataTableModelUserPessoa<UsuarioPessoa> getList() {		
		montarGrafico();
		return list;
	}


	public EmailUser getEmailUser() {
		return emailUser;
	}


	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}


	public String getCampoPesquisa() {
		return campoPesquisa;
	}


	public void setCampoPesquisa(String campoPesquisa) {
		this.campoPesquisa = campoPesquisa;
	}




	
	
	
	
	

}
