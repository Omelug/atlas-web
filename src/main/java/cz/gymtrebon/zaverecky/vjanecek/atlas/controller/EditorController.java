package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.GroupForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.RepresentativeForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
public class EditorController {
    private final AtlasService service;
    private final UDRlinkRepository udrlinkRepository;

    @GetMapping("/group/newItem")
    public String newSkupina(Principal principal,Model model,
            @RequestParam("idParentGroup") Optional<Integer> idParentGroup ) {
        GroupForm form = new GroupForm();
        if (idParentGroup.isPresent()) {
            form.setIdParentGroup(idParentGroup.get());
        }
        model.addAttribute("groups", service.seznamSkupin());
        model.addAttribute("group", form);
        model.addAttribute("newItem", true);

        AtlasController.addDatabaseList(principal, model, udrlinkRepository);
        return "group-form";
    }

    @GetMapping("/group/{id}/edit")
    public String editGroup(Principal principal,
            @PathVariable("id") Integer id,
            Model model) {

        Group group = service.najdiSkupinuDleId(id);
        GroupForm form = new GroupForm();
        form.setId(group.getId());
        form.setIdParentGroup(group.getIdParentGroup());
        form.setName(group.getName());
        form.setText(group.getText());

        model.addAttribute("groups", service.seznamSkupin());
        model.addAttribute("group", form);
        model.addAttribute("newItem", false);

        AtlasController.addDatabaseList(principal, model, udrlinkRepository);
        return "group-form";
    }
    //rekurzivni
    @PostMapping(value="/group/save", params="action-delete")
    public String smazaniSkupiny(
            Model model,
            @ModelAttribute("group") GroupForm form,
            BindingResult bindingResult) {
        log.info("form id  is " + form.getId() );
        if (!(form.getId() == null)) {
            service.smazatSkupinu(form.getId());
        }
        if ( form.getIdParentGroup() == null) {
            return "redirect:/home";
        }
        return "redirect:/group/" + form.getIdParentGroup();
    }
    @PostMapping(value="/group/save", params="action-back")
    public String editaceSkupinyZpet(
            Model model,
            @ModelAttribute("group") GroupForm form,
            BindingResult bindingResult) {
        boolean novaSkupina = form.getId() == null;
        if (novaSkupina) {
            return "redirect:/group/" + form.getIdParentGroup();
        }
        return "redirect:/group/" + form.getId();
    }
    @PostMapping(value="/group/save", params="action-save")
    public String editaceSkupinyUlozit(
            Model model,
            @Valid @ModelAttribute("group") GroupForm form,
            BindingResult bindingResult) {

        boolean novaSkupina = form.getId() == null;
        model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());

        if (bindingResult.hasErrors()) {
            return "redirect:/group/newItem";
        }

        Integer idSkupiny = null;
        if (novaSkupina) {
            idSkupiny = service.vytvoritSkupinu(
                    form.getIdParentGroup(),
                    form.getName(),
                    form.getText());

        } else {
            idSkupiny = service.ulozSkupinu(
                    form.getIdParentGroup(),
                    form.getId(),
                    form.getName(),
                    form.getText());
        }
        return "redirect:/group/" + idSkupiny;
    }
    @GetMapping("/representative/newItem")
    public String newRepresentative(Principal principal,
            @RequestParam("idParentGroup") Optional<Integer> idParentGroup,
            Model model) {

        RepresentativeForm form = new RepresentativeForm();
        if (idParentGroup.isPresent()) {
            form.setIdParentGroup(idParentGroup.get());
        }
        model.addAttribute("groups", service.seznamSkupin());
        model.addAttribute("representative", form);
        model.addAttribute("newItem", true);

        AtlasController.addDatabaseList(principal, model, udrlinkRepository);
        return "representative-form";
    }

    @GetMapping("/representative/{id}/edit")
    public String editRepresentative(Principal principal,
            @PathVariable("id") Integer id,
                                     Model model) {

        Representative representative = service.najdiRepresentativeDleId(id);
        RepresentativeForm form = new RepresentativeForm();
        form.setId(representative.getId());
        form.setIdParentGroup(representative.getIdParentGroup());
        form.setName(representative.getName());
        form.setName2(representative.getName2());
        form.setAuthor(representative.getAuthor());
        form.setColor(representative.getColor());
        form.setText(representative.getText());
        form.setImages(representative.getImages());

        model.addAttribute("groups", service.seznamSkupin());
        model.addAttribute("representative", form);
        model.addAttribute("newItem", false);

        AtlasController.addDatabaseList(principal, model, udrlinkRepository);
        return "redirect:/representative-form";
    }

    @PostMapping(value="/representative/save", params="action-delete")
    public String smazaniRepresentative(
            Model model,
            @ModelAttribute("representative") RepresentativeForm form,
            BindingResult bindingResult) {
        if (!(form.getId() == null)) {
            service.smazatPolozku(form.getId());
        }

        return "redirect:/group/" + form.getIdParentGroup();
    }

    @PostMapping(value="/representative/save", params="action-back")
    public String editaceRepresentativeZpet(
            Model model,
            @ModelAttribute("representative") RepresentativeForm form,
            BindingResult bindingResult) {
        boolean newRepresentative = form.getId() == null;
        if (newRepresentative) {
            return "redirect:/"+ form.getIdParentGroup();
        }
        return "redirect:/representative/" + form.getId();
    }

    @PostMapping(value="/representative/save", params="action-save")
    public String editaceRepresentativeUlozit(
            Model model,
            @Valid @ModelAttribute("representative") RepresentativeForm form,
            BindingResult bindingResult) {

        boolean newRepresentative = form.getId() == null;
        model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());

        if (bindingResult.hasErrors()) {
            return "redirect:/representative-form";
        }

        Integer idRepresentative = null;
        if (newRepresentative) {
            idRepresentative = service.vytvoritRepresentative(
                    form.getIdParentGroup(),
                    form.getName(),
                    form.getName2(),
                    form.getAuthor(),
                    form.getColor(),
                    form.getText()
            );
        } else {
            idRepresentative = service.ulozRepresentative(
                    form.getIdParentGroup(),
                    form.getId(),
                    form.getName(),
                    form.getName2(),
                    form.getAuthor(),
                    form.getColor(),
                    form.getText()
            );
        }
        return "redirect:/representative/" + idRepresentative;
    }
    @PostMapping("/representative/{ItemId}/upload")
    public String uploadImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable("ItemId") Integer ItemId,
            ModelMap modelMap) throws IOException {
        service.uploadImage(ItemId, file);

        return "redirect:/representative/" + ItemId;
    }

    @GetMapping("/representative/{id}/delete/{Imageid}")
    public String deleteImage(
            @PathVariable("id") Integer id,
            @PathVariable("Imageid") Integer Imageid,
            ModelMap modelMap) throws IOException {

        service.deleteImage(id, Imageid);

        return "redirect:/representative/" + id;
    }
}
