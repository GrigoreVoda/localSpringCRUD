package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Doc;
import com.grigore.mongo.repository.DocsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocService {
    private final DocsRepository docsRepository;

    public DocService(DocsRepository docsRepository){
        this.docsRepository = docsRepository;
    }
    public List<Doc> findAllDocs(){

        List<Doc> allDocs = docsRepository.findAll();
        List<Doc> allDocsSorted = allDocs.stream().sorted((
                (o1, o2) -> o1.getExpireDate().compareTo(o2.getExpireDate()))).toList();
        return  allDocsSorted;
    }
    public Doc findDocById(String id){

        return docsRepository.findDocsById(id).orElseThrow(
                ()-> new UserNotFoundException("Entity by id " + id + " not found")
        );
    }
    public Doc addDoc(Doc doc){
        docsRepository.save(doc);
        return doc;
    }
    public Doc updateDoc(Doc doc){
        docsRepository.save(doc);
        return doc;
    }
    public void deleteDoc(String id){
        if(docsRepository.findDocsById(id).isPresent()){
            docsRepository.deleteById(id);
        }

    }
}
