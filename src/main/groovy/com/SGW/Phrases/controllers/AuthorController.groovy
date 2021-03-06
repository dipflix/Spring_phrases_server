package com.SGW.Phrases.controllers

import com.SGW.Phrases.models.ApiResponse
import com.SGW.Phrases.models.Author
import com.SGW.Phrases.models.responses.AuthorResponse
import com.SGW.Phrases.repositories.AuthorRepository
import com.SGW.Phrases.repositories.PhraseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.validation.Validator

@RestController
@RequestMapping("api/authors")

@CrossOrigin(origins = "*", allowedHeaders = "*")
class AuthorController {

    private Validator validator;
    private AuthorRepository repository;

    @Autowired
    AuthorController(Validator validator,
                     AuthorRepository authorRepository) {
        repository = authorRepository
        this.validator = validator
    }

    @GetMapping
    def getAllAuthors() {
        return repository.findAll()
    }

    @PostMapping
    def create(@ModelAttribute AuthorResponse response) {
        def errors = ApiResponse.ConvertValidatorToJSON(validator.validate(response))

        if (errors.size() > 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.ErrorMessage(errors))


        Author author = new Author()
        author.setFirstName(response.getFirstName())
        author.setLastName(response.getLastName())

        def res = repository.save(author)

        return ResponseEntity
                .ok(ApiResponse.SuccessMessage(
                        "Author was create", res
                ))

    }

    @GetMapping("/{id}")
    def getAuthor(@PathVariable("id") long authorId) {
        Author author = repository.findById(authorId).get()
        return author
    }

    @PutMapping("/{id}")
    def update(@PathVariable("id") long authorId, @ModelAttribute AuthorResponse response) {
        def errors = ApiResponse.ConvertValidatorToJSON(validator.validate(response))

        if (errors.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.ErrorMessage(errors))
        }

        Author author = repository.findById(authorId).get()
        author.setFirstName(response.getFirstName())
        author.setLastName(response.getLastName())

        return ResponseEntity
                .ok(ApiResponse.SuccessMessage(
                        "Author was update!",
                        repository.save(author)
                ))
    }

    @DeleteMapping("/{id}")
    def delete(@PathVariable("id") long id) {
        def author = repository.findById(id).get()
        repository.delete(author)

        return ResponseEntity.ok(author)
    }


}
