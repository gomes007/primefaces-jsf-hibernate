package managedBean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import dao.DaoGeneric;
import dao.DaoPessoa;
import model.Pessoa;
import model.Telefone;

@ManagedBean(name = "pessoaBean" )
@ViewScoped
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private Telefone telefone = new Telefone();
	private DaoGeneric<Pessoa> daoGenericPessoa = new DaoGeneric<>();
	private DaoGeneric<Telefone> daoGenericFone = new DaoGeneric<>();
	private DaoPessoa<Pessoa> daoGeneric = new DaoPessoa<Pessoa>();
	
	private List<Pessoa> list = new ArrayList<Pessoa>();
	public List<Pessoa> getList() {
		return list;
	}
	
	private BarChartModel barChartModel = new BarChartModel();
	public BarChartModel getBarChartModel() {
		return barChartModel;
	}
	

	

	
	@PostConstruct
	private void init() {// para carregar dados do dataTable e do grafico
		
		barChartModel = new BarChartModel();
		
		list = daoGeneric.listar(Pessoa.class);
		
		//criar grafico
		ChartSeries pessoaSalario = new ChartSeries(); //cria o grupo de funcionarios
		
		for (Pessoa pessoa : list) {
			pessoaSalario.set(pessoa.getNome(), pessoa.getSalario());						
		}
		
		barChartModel.addSeries(pessoaSalario); //adiciona o grupo
		barChartModel.setTitle("grafico de salario dos funcionarios");			
		
	}
	
	
	//para mostrar dados no dataTable
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	
	/*metodo usado para carregar pessoas na tela do datatable apos a tela abrir ou efetuar alguma acao
	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGenericPessoa.getListEntity(Pessoa.class);
	}
	*/	
	

	

	public String salvar() {
		daoGenericPessoa.salvar(pessoa);
		list.add(pessoa);
		init();
		pessoa = new Pessoa();		
		return "";
	}
	
	public String atualizar() {
		daoGenericPessoa.updateMerge(pessoa);
		init();
		pessoa = new Pessoa();
		return "";
	}	
	
	
	public String removerPessoa() throws Exception {
		daoGeneric.removerUsuario(pessoa);
		list.remove(pessoa);
		pessoa = new Pessoa();
		return "";
	}
	
	public String novo() {
		pessoa = new Pessoa();
		return "";
	}
	
	
	public void addFone() {
		telefone.setPessoa(pessoa);//vincula o usuario acionado
		telefone = daoGenericFone.updateMerge(telefone);//salva ou atualiza no banco e volta mesma tela
		pessoa.getTelefone().add(telefone); //adiciona para a lista de telefone do usuario
		telefone = new Telefone();
	}
	
	
	public void removerFone() throws Exception {
		String codigofone = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("codigofone"); //para pegar o parametro
		
		Telefone removerId = new Telefone(); //instanciar para ter acesso ao Id
		removerId.setId(Long.parseLong(codigofone)); //faz o campo id receber o id que vem do parametro convertendo para long
		daoGenericFone.deletarPorId(removerId); //passa o id a ser removido  para o metodo deletar por id para apagar do banco de dados
		pessoa.getTelefone().remove(removerId); //remove da lista de telefones do usuario que esta na tela
		telefone = new Telefone();
				
	}
	

	
	
	public String atualizarFone() {
		daoGenericFone.updateMerge(telefone);
		telefone = new Telefone();
		return "";
	}

	
	
	//--------------------------------------
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Telefone getTelefone() {
		return telefone;
	}
	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}
	public DaoGeneric<Pessoa> getDaoGenericPessoa() {
		return daoGenericPessoa;
	}
	public void setDaoGenericPessoa(DaoGeneric<Pessoa> daoGenericPessoa) {
		this.daoGenericPessoa = daoGenericPessoa;
	}
	public DaoGeneric<Telefone> getDaoGenericFone() {
		return daoGenericFone;
	}
	public void setDaoGenericFone(DaoGeneric<Telefone> daoGenericFone) {
		this.daoGenericFone = daoGenericFone;
	}
	
	

}
