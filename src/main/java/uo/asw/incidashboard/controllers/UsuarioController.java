package uo.asw.incidashboard.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uo.asw.dbManagement.model.Incidencia;
import uo.asw.dbManagement.model.Usuario;
import uo.asw.dbManagement.model.ValorLimite;
import uo.asw.incidashboard.services.IncidenciasService;
import uo.asw.incidashboard.services.UsuarioService;
import uo.asw.incidashboard.services.ValorLimiteService;

@Controller
public class UsuarioController {

	@Autowired
	private IncidenciasService incidenciaService;
	@Autowired
	private ValorLimiteService valorLimiteService;
	@Autowired
	private UsuarioService usuarioService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "login";
	}

	@RequestMapping("/users/admin")
	public String getListado(Model model, Pageable pageable, Principal principal) {
		Page<Usuario> operarios = new PageImpl<Usuario>(new LinkedList<Usuario>());
		operarios = usuarioService.findAll(pageable);
		
		Page<ValorLimite> valoresLimite = new PageImpl<ValorLimite>(new LinkedList<ValorLimite>());
		valoresLimite = valorLimiteService.findAll(pageable);
		
		model.addAttribute("valoresList", valoresLimite.getContent());
		for(ValorLimite vL:valoresLimite) {
			if (vL.getPropiedad().equals("TEMPERATURA")) {
				model.addAttribute("temp", vL);
				break;
			}
		}
		model.addAttribute("usuarioList", operarios.getContent());
		model.addAttribute("page", operarios);
		model.addAttribute("conectado", "operario4@prueba.es");
		return "/users/admin";
	}

	@RequestMapping("/users/admin/update") 
	public String updateList(Model model, Pageable pageable, Principal principal){
		Page<Usuario> operarios = new PageImpl<Usuario>(new LinkedList<Usuario>());
		operarios = usuarioService.findAll(pageable);
		
		Page<ValorLimite> valoresLimite = new PageImpl<ValorLimite>(new LinkedList<ValorLimite>());
		valoresLimite = valorLimiteService.findAll(pageable);
		
		model.addAttribute("valoresList", valoresLimite.getContent());
		for(ValorLimite vL:valoresLimite) {
			if (vL.getPropiedad().equals("TEMPERATURA")) {
				model.addAttribute("temp", vL);
				break;
			}
		}
		model.addAttribute("operariosList", operarios.getContent());
		model.addAttribute("page", operarios);
		model.addAttribute("conectado", "operario4@prueba.es");
		return "/users/admin :: update";
	}
	
	@RequestMapping(value = "/admin/cambiarUmbral", method = RequestMethod.POST)
	public String cambiarUmbral(Model model,Pageable pageable, @RequestBody String json) {
		
		String propiedadUmbral = json.split("%2C%22")[0].split("%22%3A%22")[1].split("%22")[0];
		String valorMaximo = json.split("%2C%22")[1].split("%22%3A%22")[1].split("%22")[0];
		String valorMinimo = json.split("%2C%22")[2].split("%22%3A%22")[1].split("%22")[0];
		String criticoMin = json.split("%2C%22")[3].split("%22%3A")[1];
		String criticoMax = json.split("%2C%22")[4].split("%22%3A")[1].split("%7D=")[0];
		valorLimiteService.update(propiedadUmbral, valorMaximo, valorMinimo, criticoMax, criticoMin);
		
		return "redirect:/users/admin";
	}
	

	@RequestMapping("/users/analisis")
	public String getResponsable(Model model, Pageable pageable, Principal principal) {
		
		List<Incidencia> incidencias = incidenciaService.getAllIncidencias();
		model.addAttribute("incidenciasList", incidencias);
		model.addAttribute("page", incidencias);
		model.addAttribute("datos", incidenciaService.getNum(incidencias));
		model.addAttribute("fechas", incidenciaService.getDays(incidencias));

		int numMax =0;
		for(int i=0;i<incidenciaService.getNum(incidencias).length;i++) {
			if(numMax < incidenciaService.getNum(incidencias)[i]) numMax = incidenciaService.getNum(incidencias)[i];
		}
		model.addAttribute("max", numMax+2);
		model.addAttribute("gCircular", incidenciaService.getDataCircle());
		return "/users/analisis";
	}
	
	@RequestMapping(value="/refresh-table")
	public String getValuesTable(Model model) {
		List<Incidencia> incis = incidenciaService.getLInciKafka();
	    model.addAttribute("dataTable",incis);
	    model.addAttribute("vCritics", incidenciaService.getIdWithCritis(incis));
	    return "/users/analisis :: #myInLineTable";
	}

	@RequestMapping("/users/operario")
	public String getOperarios(Model model, Pageable pageable, Principal principal) {
		String mail = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario user = usuarioService.getUsuarioByMail(mail);
		if(user ==null) {
			return "redirect:/login";
		}
		Page<Incidencia> incidencias = new PageImpl<Incidencia>(new LinkedList<Incidencia>());
		incidencias = incidenciaService.getUserIncidencias(pageable, user);
		model.addAttribute("incidenciasList", incidencias.getContent());
		model.addAttribute("nameUser", "          Incidencias de " + user.getNombre());
		model.addAttribute("page", incidencias);
		return "/users/operario";
	}	 //
	
	
	@RequestMapping("/users/operario/update") 
	public String updateListInci(Model model, Pageable pageable, Principal principal){
		String mail = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario user = usuarioService.getUsuarioByMail(mail);
		Page<Incidencia> incidencias = new PageImpl<Incidencia>(new LinkedList<Incidencia>());
		incidencias = incidenciaService.getUserIncidencias(pageable, user);
		model.addAttribute("incidenciasList", incidencias.getContent());
		model.addAttribute("nameUser", "          Incidencias de " + user.getNombre());
		model.addAttribute("page", incidencias);
		return "/users/operario :: tableInci";
	}
	
	@RequestMapping(value="/users/{id}/cambiarEstado/{nuevoEstado}", method=RequestMethod.GET) 
	public String sendResquest(Model model, @PathVariable String id , @PathVariable String nuevoEstado, Principal principal){
		incidenciaService.changeState(id, nuevoEstado);

		return "redirect:/users/operario";
	}
	
	@RequestMapping(value="/refresh-alert")
	public String getValuesAlert(Model model) {
		List<Incidencia> incis = incidenciaService.getLInciKafka();
		
		
		
	    model.addAttribute("inicidencias",incidenciaService.getAllIncidencias());
	    model.addAttribute("num", 1);
	    return "/users/operario :: notificaciones";
	}
}
