package uo.asw.incidashboard.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import uo.asw.dbManagement.model.Propiedad;
@EnableMongoRepositories
public interface PropiedadRepository extends MongoRepository<Propiedad, ObjectId>{

}