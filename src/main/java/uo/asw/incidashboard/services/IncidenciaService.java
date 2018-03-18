package uo.asw.incidashboard.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uo.asw.dbManagement.model.Incidencia;
import uo.asw.dbManagement.model.Incidencia;
import uo.asw.incidashboard.repositories.IncidenciaRepository;
import uo.asw.incidashboard.repositories.IncidenciaRepository;

@Service
public class IncidenciaService {
	@Autowired
	private IncidenciaRepository incidenciaRepository;
	
	public List<Incidencia> getIncidencias() {
		List<Incidencia> incidencias= new ArrayList<Incidencia>();
		incidenciaRepository.findAll().forEach(incidencias::add);
		return incidencias;
	}
	public Incidencia getIncidenciaByNombre(String nombre) {
		return incidenciaRepository.findByNombre(nombre);
	}

	public void addIncidencia(Incidencia incidencia) {
		
		incidenciaRepository.save(incidencia);
	}

	public void deleteIncidencia(Long id) {
		incidenciaRepository.deleteById(id);
	}
}
