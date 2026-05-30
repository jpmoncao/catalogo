package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.services.CategoriaService;
import br.com.fatec.catalogo.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Autowired
    private ProdutoService service;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(value = "nome", required = false) String nome,
                         @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                         Model model) {

        if (nome != null && !nome.isBlank()) {
            model.addAttribute("produtos", service.listarPorNome(nome));
        } else if (categoriaId != null) {
            model.addAttribute("produtos", service.listarPorCategoria(categoriaId));
        } else {
            model.addAttribute("produtos", service.listarTodos());
        }

        model.addAttribute("categorias", categoriaService.listarTodas());

        return "lista-produtos";
    }

    @GetMapping("/auditoria")
    @PreAuthorize("hasRole('ADMIN')")
    public String auditoria(Model model) {
        model.addAttribute("produtos", service.listarPorDataAtualizacao());
        return "auditoria-produtos";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "cadastro-produto";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute("produto") ProdutoModel produto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "cadastro-produto";
        }

        try {
            service.salvar(produto);
        } catch (IllegalArgumentException e) {
            result.rejectValue("quantidade", "error.quantidade", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "cadastro-produto";
        }

        redirectAttributes.addFlashAttribute("sucesso",
                "Produto salvo com sucesso em " + FORMATTER.format(LocalDateTime.now()));
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "cadastro-produto";
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable long id) {
        service.excluir(id);
        return "redirect:/produtos";
    }
}
