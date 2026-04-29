package dev.gustavo.agendamento.controller;

import dev.gustavo.agendamento.dto.AgendamentoCreateRequest;
import dev.gustavo.agendamento.dto.AgendamentoResponse;
import dev.gustavo.agendamento.dto.AgendamentoUpdateRequest;
import dev.gustavo.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {
    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service){
        this.service = service;
    }

    @PostMapping
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoCreateRequest request){
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable long id, @Valid @RequestBody AgendamentoUpdateRequest request){
        return  service.atualizar(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id){
        return service.cancelar(id);
    }

    @PutMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id) {
        return service.concluir(id);
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}