package pt.jorgeduarte.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.services.FileReaderTxtService;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/output")
public class FileHandlerController {
    FileReaderTxtService fileReaderService;

    @RequestMapping("/txt/{name}")
    public ResponseEntity<String> readFileData(@PathVariable String name) throws FileNotFoundException {
        return ResponseEntity.ok().body(fileReaderService.read(name));
    }
    @RequestMapping("/html/{name}")
    public ResponseEntity<String> readFileDataHtml(@PathVariable String name){
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
