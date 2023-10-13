package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.LoggerLine;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Request;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.ItemForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.CustomLoggerRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RequestRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.ColorService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.UserFindService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
public class EditorController {
    private final ItemRepository itemRepository;
    private final AtlasService service;
    private final AtlasController atlasController;
    private final RequestRepository requestRepository;
    private final SpringTemplateEngine templateEngine;
    private final UserFindService userFindService;
    private final ColorService colorService;
    private final CustomLoggerRepository customLoggerRepository;

    @GetMapping("/requests")
    public String requests(Principal principal,Model model) {

        model.addAttribute("requestList", requestRepository.findAll());
        model.addAttribute("selectedRequest", null);
        atlasController.addBase(principal, model);
        return "editor/requests";
    }

    @GetMapping("/{type}/newItem")
    public String newItem(Principal principal,
                           Model model,
                           @RequestParam("idParentGroup") Optional<Long> idParentGroup,
                           @RequestParam("open") boolean open,
                           @PathVariable String type) {
        ItemForm form = new ItemForm();
        form.setFormType(type);
        if (idParentGroup.isPresent()) {
            form.setIdParentGroup(idParentGroup.get());
            model.addAttribute("originalParentId", idParentGroup.get());
            model.addAttribute("breadcrumbs", service.getBreadCrumbs(idParentGroup.get()));
        }
        userFindService.setOpen(principal.getName(), open);
        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("itemForm", form);
        model.addAttribute("colorsSelected", new ArrayList<String>());
        model.addAttribute("newItem", true);

        atlasController.addBase(principal, model);

        if(type.equals("GROUP")){
            return "group-form";
        }
        if(type.equals("REPRESENTATIVE")){
            return "representative-form";
        }
        customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "User tried create item of invalid type "+type));
        return "redirect-form";
    }

    /*@PostMapping(value="/item/save", params="action-save")
    public String groupEditSave(Principal principal, @RequestParam(name = "colors", required = false) List<String> colors, Model model, @Valid @ModelAttribute("itemForm") ItemForm itemForm, BindingResult bindingResult) {
        System.out.println("gggggggg" + itemForm.getFormType());
        boolean newGroup = (itemForm.getId() == null);
        model.addAttribute("groups", service.breadCrumbList());

        if (bindingResult.hasErrors()) {
            breadCrumbsCorrection(model, itemForm.getIdParentGroup());
            atlasController.addBase(principal, model);
            itemForm.setFormType(Typ.GROUP.name());
            model.addAttribute("itemForm", itemForm);
            if (colors != null && !colors.isEmpty()) {
                model.addAttribute("colorsSelected", colors);
            }else {
                model.addAttribute("colorsSelected", new ArrayList<String>());
            }
            return "group-form";
        }
        Long groupId;
        if (newGroup) {
            groupId = service.createGroup(
                    itemForm.getIdParentGroup(),
                    itemForm.getName(),
                    itemForm.getName2(),
                    itemForm.getText(),
                    colorService.getColorsObject(colors));

        } else {
            groupId = service.saveGroup(
                    itemForm.getIdParentGroup(),
                    itemForm.getId(),
                    itemForm.getName(),
                    itemForm.getName2(),
                    itemForm.getText(),
                    colorService.getColorsObject(colors));
        }
        return "redirect:/item/" + groupId;
    }*/


    @PostMapping(value="/item/save", params="action-save")
    public String itemEditSave(@RequestParam(name = "colors", required = false) List<String> colors, Model model, @Valid @ModelAttribute("itemForm") ItemForm itemForm, BindingResult bindingResult) {

        System.out.println("fgh typ" + itemForm.getFormType());
        model.addAttribute("groups", service.breadCrumbList());
        Typ typ = ItemForm.getType(itemForm.getFormType());
        if (typ == Typ.ROOT){
            //TODO predelat
        }

        if (bindingResult.hasErrors()) {
            breadCrumbsCorrection(model, itemForm.getIdParentGroup());
            if (colors != null && !colors.isEmpty()) {
                model.addAttribute("colorsSelected", colors);
            }
            return "group-form";
        }

        boolean newItem = (itemForm.getId() == null);
        Long itemId;
        if (newItem) {
            if (typ == Typ.ROOT){
                return "redirect:/home";
            }
            itemId = service.createItem(typ,
                    itemForm.getIdParentGroup(),
                    itemForm.getName(),
                    itemForm.getName2(),
                    itemForm.getAuthor(),
                    itemForm.getText(),
                    colorService.getColorsObject(colors));

        } else {
            itemId = service.saveGroup(typ,
                    itemForm.getIdParentGroup(),
                    itemForm.getId(),
                    itemForm.getName(),
                    itemForm.getName2(),
                    itemForm.getAuthor(),
                    itemForm.getText(),
                    colorService.getColorsObject(colors));
        }
        return "redirect:/item/" + itemId;
    }

    private void breadCrumbsCorrection(Model model, Long idParentGroup) {
        if (idParentGroup == null) {
            Long originalParentId = (Long) model.getAttribute("originalParentId");
            if(originalParentId != null) {
                model.addAttribute("breadcrumbs", service.getBreadCrumbs(originalParentId));
            }
        }else{
            model.addAttribute("breadcrumbs", service.getBreadCrumbs(idParentGroup));
        }

    }

    @GetMapping("/item/{id}/edit")
    public String editItem(Principal principal, @PathVariable("id") Long id, Model model) {

        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()){
            return "redirect:/home";
        }

        ItemForm form = new ItemForm(item.get());
        model.addAttribute("itemForm", form);
        model.addAttribute("groups", service.breadCrumbList());
        model.addAttribute("newItem", false);
        atlasController.addBase(principal, model);
        model.addAttribute("breadcrumbs", service.getBreadCrumbs(item.get().getId()));

        if ((item.get().getTyp() == Typ.GROUP) || (item.get().getTyp() == Typ.ROOT)){
            return "group-form";
        }
        if (item.get().getTyp() == Typ.REPRESENTATIVE){
            model.addAttribute("representative", service.ItemToRepresentative(item.get()));
            model.addAttribute("colorsSelected", colorService.colorsToString(item.get().getColors()));
            return "representative-form";
        }
        customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "User try edit the item with type " + item.get().getTyp()));
        return "errors/error404";
    }

    @PostMapping(value="/{type}/save", params="action-delete")
    public String groupDelete(Principal principal, @ModelAttribute("itemForm") ItemForm form, @PathVariable String type) {
        Typ typ = ItemForm.getType(type);
        if (typ == Typ.ROOT) {
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "Editor tried delete root, it is not allowed"));
            return "redirect:/home";
        }
        if (typ == Typ.GROUP) {
            service.removeGroup(form.getId());
        }
        if (typ == Typ.REPRESENTATIVE) {
            service.removeItem(form.getId());
        }
        return "redirect:/item/" + form.getIdParentGroup();
    }

    @PostMapping(value="/item/save", params="action-back")
    public String itemEditBack(
            Model model,
            @ModelAttribute("itemForm") ItemForm form) {
        boolean newItem = form.getId() == null;
        if (newItem) {
            return "redirect:/item/" + form.getIdParentGroup();
        }
        return "redirect:/item/" + form.getId();
    }

    /*

    @PostMapping(value="/item/save", params="action-save")
    public String representativeEditSave(Principal principal,
            Model model, @RequestParam("colors") List<String> colors,
            @Valid @ModelAttribute("itemForm") ItemForm form,
            BindingResult bindingResult) {

        boolean newItem = form.getId() == null;
        model.addAttribute("groups", service.breadCrumbList());

        if (bindingResult.hasErrors()) {
            atlasController.addBase(principal, model);
            if (colors != null && !colors.isEmpty()) {
                model.addAttribute("colorsSelected", colors);
            }else {
                model.addAttribute("colorsSelected", new ArrayList<String>());
            }
            return "representative-form";
        }

        Long idRepresentative;
        if (newItem) {
            idRepresentative = service.createRepresentative(
                    form.getIdParentGroup(),
                    form.getName(),
                    form.getName2(),
                    form.getAuthor(),
                    colorService.getColorsObject(colors),
                    form.getText()
            );
        } else {
            idRepresentative = service.saveRepresentative(
                    form.getIdParentGroup(),
                    form.getId(),
                    form.getName(),
                    form.getName2(),
                    form.getAuthor(),
                    colorService.getColorsObject(colors),
                    form.getText()
            );
        }
        return "redirect:/item/" + idRepresentative;
    }*/
    @PostMapping("/representative/{ItemId}/upload")
    public String uploadImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable("ItemId") Long ItemId) {
        service.uploadImage(ItemId, file);
        return "redirect:/item/" + ItemId;
    }

    @GetMapping("/representative/{id}/delete/{imageId}")
    public String deleteImage(
            @PathVariable("id") Long id,
            @PathVariable("imageId") Long imageId) {
        service.deleteImage(imageId);
        return "redirect:/item/" + id;
    }

    @PostMapping(value="/editor/selectRequest")
    @ResponseBody
    public String selectRequest(HttpServletRequest request, HttpServletResponse response, @RequestParam("requestMark") String requestMark) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        Optional<Request> selectedRequest = requestRepository.findByRequestMark(requestMark);
        context.setVariable("selectedRequest", selectedRequest.orElse(null));
        selectedRequest.ifPresent(value -> log.info("Sent request: " + value.getRequestMark()));
        String text = templateEngine.process("editor/selectedRequest.html", context);
        log.info(text);
        return templateEngine.process("editor/selectedRequest.html", context);
    }
}
