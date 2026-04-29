package dev.gustavo.agendamento.service;

import dev.gustavo.agendamento.dto.AgendamentoCreateRequest;
import dev.gustavo.agendamento.dto.AgendamentoResponse;
import dev.gustavo.agendamento.dto.AgendamentoUpdateRequest;
import dev.gustavo.agendamento.mapper.AgendamentoMapper;
import dev.gustavo.agendamento.model.Agendamento;
import dev.gustavo.agendamento.model.StatusAgendamento;
import dev.gustavo.agendamento.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AgendamentoService {
    private final AgendamentoRepository repo;

    public AgendamentoService(AgendamentoRepository repo){
        this.repo = repo;
    }

    private void validarIntervalo(LocalDateTime inicio, LocalDateTime fim){
        if(inicio == null || fim == null || !inicio.isBefore(fim)){
            throw new IllegalArgumentException("intervalo invalido: data inicio deve ser anterior a data fim");
        }
    }

    private void checarConflito(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id){
        if (repo.existsConflito(usuario, inicio, fim, id, StatusAgendamento.AGENDADO)){
            throw new IllegalArgumentException("conflito de agendamento: o usuario ja possui um agendamento nesse intervalo");
        }
    }

    @Transactional
    public AgendamentoResponse criar(@Valid AgendamentoCreateRequest req){
        validarIntervalo(req.dataInicio(), req.dataFim());
        checarConflito(req.usuario(), req.dataInicio(), req.dataFim(), null);

        Agendamento entity = AgendamentoMapper.toEntity(req);
        entity = repo.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse atualizar(Long id, AgendamentoUpdateRequest req){
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("agendamento nao encontrado"));
       AgendamentoMapper.merge(entity, req);
       validarIntervalo(req.dataInicio(), req.dataFim());
       checarConflito(entity.getUsuario(), req.dataInicio(), req.dataFim(), entity.getId());

       entity = repo.save(entity);
       return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        return AgendamentoMapper.toResponse(a);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id) {
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        entity.setStatus(StatusAgendamento.CANCELADO);
        entity = repo.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse concluir(Long id) {
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        entity.setStatus(StatusAgendamento.CONCLUIDO);
        entity = repo.save(entity);

        return AgendamentoMapper.toResponse(entity);
    }
}
