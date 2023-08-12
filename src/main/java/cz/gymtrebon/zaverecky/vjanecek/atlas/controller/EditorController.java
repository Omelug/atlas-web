package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Request;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.GroupForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.RepresentativeForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RequestRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
public class EditorController {
    private final AtlasService service;
    private final AtlasController atlasController;
    private final RequestRepository requestRepository;
    private final SpringTemplateEngine templateEngine;

    @GetMapping("/requests")
    public String requests(Principal principal,Model model) {

        model.addAttribute("requestList", requestRepository.findAll());
        model.addAttribute("selectedRequest", null);
        atlasController.addBase(principal, model);
        return "editor/requests";
    }

    @GetMapping("/group/newItem")
    public String newGroup(Principal principal,Model model,
            @RequestParam("idParentGroup") Optional<Integer> idParentGroup ) {
        GroupForm form = new GroupForm();
        if (idParentGroup.isPresent()) {
            form.setIdParentGroup(idParentGroup.get());
            model.addAttribute("originalParentId", idParentGroup.get());
            model.addAttribute("breadcrumbs", service.getBreadCrumbs(idParentGroup.get()));
        }
        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("group", form);
        model.addAttribute("newItem", true);

        atlasController.addBase(principal, model);
        return "group-form";
    }
    @PostMapping(value="/group/save", params="action-save")
    public String groupEditSave(Principal principal, Model model, @Valid @ModelAttribute("group") GroupForm form, BindingResult bindingResult) {

        boolean newGroup = form.getId() == null;
        model.addAttribute("groups", service.breadCrumbList());

        if (bindingResult.hasErrors()) {
            breadCrumbsCorrection(model, form.getIdParentGroup());
            atlasController.addBase(principal, model);
            return "group-form";
        }

        Integer groupId;
        if (newGroup) {
            groupId = service.createGroup(
                    form.getIdParentGroup(),
                    form.getName(),
                    form.getText());

        } else {
            groupId = service.saveGroup(
                    form.getIdParentGroup(),
                    form.getId(),
                    form.getName(),
                    form.getText());
        }
        return "redirect:/group/" + groupId;
    }

    private void breadCrumbsCorrection(Model model, Integer idParentGroup) {
        if (idParentGroup == null) {
            Integer originalParentId = (Integer) model.getAttribute("originalParentId");
            if(originalParentId != null) {
                model.addAttribute("breadcrumbs", service.getBreadCrumbs(originalParentId));
                return;
            }
        }
        model.addAttribute("breadcrumbs", service.getBreadCrumbs(idParentGroup));

    }


    @GetMapping("/group/{id}/edit")
    public String editGroup(Principal principal,
            @PathVariable("id") Integer id,
            Model model) {

        Group group = service.findGroupById(id);
        GroupForm form = new GroupForm();
        form.setId(group.getId());
        form.setIdParentGroup(group.getIdParentGroup());
        form.setName(group.getName());
        form.setText(group.getText());

        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("group", form);
        model.addAttribute("newItem", false);

        atlasController.addBase(principal, model);
        return "group-form";
    }
    //recursive
    @PostMapping(value="/group/save", params="action-delete")
    public String groupDelete(
            @ModelAttribute("group") GroupForm form) {
        log.info("form id  is " + form.getId() );
        if (!(form.getId() == null)) {
            service.removeGroup(form.getId());
        }
        if ( form.getIdParentGroup() == null) {
            return "redirect:/home";
        }
        return "redirect:/group/" + form.getIdParentGroup();
    }
    @PostMapping(value="/group/save", params="action-back")
    public String groupEditBack(
            Model model,
            @ModelAttribute("group") GroupForm form) {
        boolean newGroup = form.getId() == null;
        if (newGroup) {
            Integer originalParentId = (Integer) model.getAttribute("originalParentId");
            if (originalParentId == null) {return "redirect:/home";}
            return "redirect:/group/" + originalParentId;
        }
        return "redirect:/group/" + form.getId();
    }
    @GetMapping("/representative/newItem")
    public String newRepresentative(Principal principal,
            @RequestParam("idParentGroup") Optional<Integer> idParentGroup,
            Model model) {

        RepresentativeForm form = new RepresentativeForm();
        if (idParentGroup.isPresent()) {
            form.setIdParentGroup(idParentGroup.get());
            model.addAttribute("originalParentId", idParentGroup.get());
        }
        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("representative", form);
        model.addAttribute("newItem", true);

        atlasController.addBase(principal, model);
        return "representative-form";
    }

    @GetMapping("/representative/{id}/edit")
    public String editRepresentative(Principal principal,
            @PathVariable("id") Integer id,
                                     Model model) {

        Representative representative = service.findRepresentativeById(id);
        RepresentativeForm form = new RepresentativeForm();
        form.setId(representative.getId());
        form.setIdParentGroup(representative.getIdParentGroup());
        form.setName(representative.getName());
        form.setName2(representative.getName2());
        form.setAuthor(representative.getAuthor());
        form.setColor(representative.getColor());
        form.setText(representative.getText());
        form.setImages(representative.getImages());

        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("representative", form);
        model.addAttribute("newItem", false);

        atlasController.addBase(principal, model);
        return "representative-form";
    }

    @PostMapping(value="/representative/save", params="action-delete")
    public String representativeDelete(
            @ModelAttribute("representative") RepresentativeForm form) {
        if (!(form.getId() == null)) {
            service.removeItem(form.getId());
        }

        return "redirect:/group/" + form.getIdParentGroup();
    }

    @PostMapping(value="/representative/save", params="action-back")
    public String representativeEditBack(
            Model model,
            @ModelAttribute("representative") RepresentativeForm form) {
        boolean newRepresentative = form.getId() == null;
        if (newRepresentative) {
            Integer originalParentId = (Integer) model.getAttribute("originalParentId");
            if (originalParentId == null) {return "redirect:/home";}
            return "redirect:/"+ form.getIdParentGroup();
        }
        return "redirect:/representative/" + form.getId();
    }

    @PostMapping(value="/representative/save", params="action-save")
    public String representativeEditSave(Principal principal,
            Model model,
            @Valid @ModelAttribute("representative") RepresentativeForm form,
            BindingResult bindingResult) {

        boolean newRepresentative = form.getId() == null;
        model.addAttribute("groups", service.breadCrumbList());

        if (bindingResult.hasErrors()) {
            atlasController.addBase(principal, model);
            return "representative-form";
        }

        Integer idRepresentative;
        if (newRepresentative) {
            idRepresentative = service.createRepresentative(
                    form.getIdParentGroup(),
                    form.getName(),
                    form.getName2(),
                    form.getAuthor(),
                    form.getColor(),
                    form.getText()
            );
        } else {
            idRepresentative = service.saveRepresentative(
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
            @PathVariable("ItemId") Integer ItemId) {
        service.uploadImage(ItemId, file);
        return "redirect:/representative/" + ItemId;
    }

    @GetMapping("/representative/{id}/delete/{imageId}")
    public String deleteImage(
            @PathVariable("id") Integer id,
            @PathVariable("imageId") Integer imageId) {

        service.deleteImage(imageId);

        return "redirect:/representative/" + id;
    }

    @PostMapping(value="/editor/selectRequest")
    @ResponseBody
    public String selectRequest(HttpServletRequest request, HttpServletResponse response, @RequestParam("requestMark") String requestMark) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        Optional<Request> selectedRequest = requestRepository.findByRequestMark(requestMark);
        context.setVariable("selectedRequest", selectedRequest.orElse(null));
        log.info("Sent request: " + selectedRequest.get().getRequestMark());
        String text = templateEngine.process("editor/selectedRequest.html", context);
        log.info(text);
        return templateEngine.process("editor/selectedRequest.html", context);
        //return "test";
    }
}
