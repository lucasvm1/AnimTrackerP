package com.lucasvm.animtrackerv2.controllers;

import com.lucasvm.animtrackerv2.dtos.CenaDTO;
import com.lucasvm.animtrackerv2.dtos.ClienteDTO;
import com.lucasvm.animtrackerv2.dtos.ProjetoDTO;
import com.lucasvm.animtrackerv2.models.CenaModel;
import com.lucasvm.animtrackerv2.models.ProjetoModel;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.services.CenaService;
import com.lucasvm.animtrackerv2.services.ClienteService;
import com.lucasvm.animtrackerv2.services.ProjetoService;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private CenaService cenaService;

    @Autowired
    private ClienteService clienteService;

    // Página principal do dashboard com métricas, filtros e listas resumidas
    @GetMapping("/dashboard")
    public String paginaDashboard(Principal principal, Model model,
                                  @RequestParam(required = false) UUID projetoIdFiltro,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicioFiltro,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFimFiltro) {

        // Autentica usuário
        UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioAutenticado);

        // Busca todos os dados do usuário (projetos, cenas, clientes)
        List<ProjetoDTO> todosProjetosUsuario = projetoService.listarTodosPorUsuario(usuarioAutenticado.getId());
        List<CenaDTO> todasCenasUsuario = cenaService.listarTodosPorUsuario(usuarioAutenticado.getId());
        List<ClienteDTO> todosClientesUsuario = clienteService.listarTodosPorUsuario(usuarioAutenticado.getId());

        // Filtra cenas e projetos pelos filtros recebidos (projeto, datas)
        List<CenaDTO> cenasFiltradasParaMetricas = todasCenasUsuario.stream()
                .filter(c -> projetoIdFiltro == null || (c.getProjetoId() != null && c.getProjetoId().equals(projetoIdFiltro)))
                .filter(c -> dataInicioFiltro == null || (c.getData_conclusao() != null && !c.getData_conclusao().isBefore(dataInicioFiltro) || c.getData_inicio() != null && !c.getData_inicio().isBefore(dataInicioFiltro)))
                .filter(c -> dataFimFiltro == null || (c.getData_conclusao() != null && !c.getData_conclusao().isAfter(dataFimFiltro) || c.getData_inicio() != null && !c.getData_inicio().isAfter(dataFimFiltro)))
                .collect(Collectors.toList());

        List<ProjetoDTO> projetosFiltradosParaMetricas = todosProjetosUsuario.stream()
                .filter(p -> projetoIdFiltro == null || p.getId().equals(projetoIdFiltro))
                .filter(p -> dataInicioFiltro == null || (p.getData_conclusao() != null && !p.getData_conclusao().isBefore(dataInicioFiltro) || p.getData_inicio() != null && !p.getData_inicio().isBefore(dataInicioFiltro)))
                .filter(p -> dataFimFiltro == null || (p.getData_conclusao() != null && !p.getData_conclusao().isAfter(dataFimFiltro) || p.getData_inicio() != null && !p.getData_inicio().isAfter(dataFimFiltro)))
                .collect(Collectors.toList());

        // Calcula métricas principais do dashboard
        long cenasAprovadas = cenasFiltradasParaMetricas.stream().filter(c -> c.getStatus() == CenaModel.StatusCena.APROVADA).count();
        long cenasEmAndamentoMetrica = cenasFiltradasParaMetricas.stream().filter(c -> c.getStatus() == CenaModel.StatusCena.EM_PRODUCAO).count();
        long cenasPausadas = cenasFiltradasParaMetricas.stream().filter(c -> c.getStatus() == CenaModel.StatusCena.PAUSADA).count();

        long projetosEmAndamento = projetosFiltradosParaMetricas.stream().filter(p -> p.getStatus() == ProjetoModel.statusProjeto.EM_ANDAMENTO).count();
        long projetosConcluidos = projetosFiltradosParaMetricas.stream().filter(p -> p.getStatus() == ProjetoModel.statusProjeto.CONCLUIDO).count();

        long totalCenasPose = cenasFiltradasParaMetricas.stream().filter(c -> c.getEstagio() == CenaModel.EstagioCena.POSE).count();
        long totalCenasAnimacao = cenasFiltradasParaMetricas.stream().filter(c -> c.getEstagio() == CenaModel.EstagioCena.ANIMACAO).count();
        long totalCenasCorrecao = cenasFiltradasParaMetricas.stream().filter(c -> c.getEstagio() == CenaModel.EstagioCena.CORRECAO).count();

        // Tempo médio de produção
        double tempoTotalProducaoMinutos = cenasFiltradasParaMetricas.stream()
                .filter(c -> c.getTempoProducao() > 0)
                .mapToDouble(CenaDTO::getTempoProducao)
                .sum();
        long cenasComTempoProducao = cenasFiltradasParaMetricas.stream().filter(c -> c.getTempoProducao() > 0).count();
        double tempoMedioPorCenaHoras = 0;
        if (cenasComTempoProducao > 0) {
            tempoMedioPorCenaHoras = (tempoTotalProducaoMinutos / cenasComTempoProducao) / 60.0;
        }

        // Métricas de duração das cenas
        BigDecimal duracaoTotalSegundosCenas = cenasFiltradasParaMetricas.stream()
                .map(CenaDTO::getDuracao)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double tempoTotalProducaoHoras = tempoTotalProducaoMinutos / 60.0;
        BigDecimal segundosPorHora = BigDecimal.ZERO;
        if (tempoTotalProducaoHoras > 0 && duracaoTotalSegundosCenas.compareTo(BigDecimal.ZERO) > 0) {
            segundosPorHora = duracaoTotalSegundosCenas.divide(BigDecimal.valueOf(tempoTotalProducaoHoras), 2, RoundingMode.HALF_UP);
        }

        long clientesCadastrados = todosClientesUsuario.size();
        long totalCenasFeitas = cenasFiltradasParaMetricas.size();

        // Adiciona métricas ao modelo para exibir no dashboard
        model.addAttribute("cenasAprovadas", cenasAprovadas);
        model.addAttribute("cenasEmAndamento", cenasEmAndamentoMetrica);
        model.addAttribute("cenasPausadas", cenasPausadas);
        model.addAttribute("projetosEmAndamento", projetosEmAndamento);
        model.addAttribute("projetosConcluidos", projetosConcluidos);
        model.addAttribute("totalCenasPose", totalCenasPose);
        model.addAttribute("totalCenasAnimacao", totalCenasAnimacao);
        model.addAttribute("totalCenasCorrecao", totalCenasCorrecao);
        model.addAttribute("tempoMedioPorCenaHoras", String.format("%.2f", tempoMedioPorCenaHoras));
        model.addAttribute("segundosPorHora", segundosPorHora);
        model.addAttribute("clientesCadastrados", clientesCadastrados);
        model.addAttribute("totalCenasFeitas", totalCenasFeitas);
        model.addAttribute("duracaoTotalSegundosCenas", duracaoTotalSegundosCenas);

        // Monta listas rápidas para cards do painel
        List<CenaDTO> cenasPainelEmAndamento = todasCenasUsuario.stream()
                .filter(c -> c.getStatus() == CenaModel.StatusCena.EM_PRODUCAO)
                .sorted(Comparator.comparing(CenaDTO::getData_previsao, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .collect(Collectors.toList());

        List<CenaDTO> cenasPainelNaoIniciadas = todasCenasUsuario.stream()
                .filter(c -> c.getStatus() == CenaModel.StatusCena.NAO_INICIADA)
                .sorted(Comparator.comparing(CenaDTO::getData_inicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .collect(Collectors.toList());

        // Mapa de nomes de projetos para exibição rápida
        Map<UUID, String> mapaNomesProjetos = todosProjetosUsuario.stream()
                .collect(Collectors.toMap(ProjetoDTO::getId, ProjetoDTO::getNome, (nomeExistente, novoNome) -> nomeExistente));

        model.addAttribute("cenasPainelEmAndamento", cenasPainelEmAndamento);
        model.addAttribute("cenasPainelNaoIniciadas", cenasPainelNaoIniciadas);
        model.addAttribute("mapaNomesProjetos", mapaNomesProjetos);

        // Filtros selecionados e projetos disponíveis para filtro
        model.addAttribute("projetosParaFiltro", todosProjetosUsuario);
        model.addAttribute("projetoIdFiltroSelecionado", projetoIdFiltro);
        model.addAttribute("dataInicioFiltroSelecionado", dataInicioFiltro);
        model.addAttribute("dataFimFiltroSelecionado", dataFimFiltro);

        return "views/dashboard/principal";
    }
}
