package com.example.minhaloja.controle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.validation.Path;
import javax.validation.Valid;

import com.example.minhaloja.modelo.Cliente;
import com.example.minhaloja.repositorios.RepositorioCliente;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.qos.logback.core.net.server.Client;

@RestController
public class ControladorCliente {

    @Autowired
    RepositorioCliente repositorioCliente;
    private java.nio.file.Path path;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView retorno = new ModelAndView("index.html");
        return retorno;
    }

    @RequestMapping("/formulario_cliente")
    public ModelAndView formularioCliente(Cliente cliente) {
        ModelAndView retorno = new ModelAndView("cadastroCliente.html");        
        return retorno;
    }

     @InitBinder
     protected void initbinder(WebDataBinder binder){
     binder.setValidator(null);
    }

    @RequestMapping("/novo_cliente")
    public ModelAndView cadastroCliente(@Valid Cliente cliente, BindingResult bidingResult, RedirectAttributes redirect,
            @RequestParam(value = "foto", required = false) MultipartFile foto){
        ModelAndView retorno;
        if(bidingResult.hasErrors()){            
            redirect.addFlashAttribute("cliente", cliente); 
            retorno = new ModelAndView("cadastroCliente.html");
            return retorno;
        }
        retorno = new ModelAndView("redirect:/");
        repositorioCliente.save(cliente);  

        if(!foto.isEmpty()){
            String path = processaArquivo(cliente, foto);
            cliente.sePathToFoto(path);
            repositorioCliente.save(cliente);
        }
        redirect.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");     
        return retorno;
    }

    private String processaArquivo(Cliente cliente, MultipartFile foto) {
        try{
            byte[] conteudo  = foto.getBytes();
             path = Paths.get("upload-dir" + File.separator + cliente.getId());
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }

            Files.write(path.resolve(foto.getOriginalFilename()), conteudo);
            return  path.resolve(foto.getOriginalFilename()).toString();
            //FileUtils.writeByteArrayToFIle(new File("ondevaificar"), conteudo);
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";
    
        }
    

}