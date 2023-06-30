package com.donjon.java_avance_1;

import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@Controller
public class PersonnageController {
    private List<Personnage> personnages = new ArrayList<>();
    private static long dernierId = 0;

//    @GetMapping("/personnages")
//    public String afficherListePersonnages(Model model) {
//        model.addAttribute("personnages", personnages);
//        return "liste_personnages";
//    }

    @GetMapping("/personnages")
    public String afficherListePersonnages(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/personnages";
        ResponseEntity<Personnage[]> response = restTemplate.getForEntity(apiUrl, Personnage[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Personnage[] personnages = response.getBody();
            model.addAttribute("personnages", personnages);
        }

        return "liste_personnages";
    }

//---------------------------------------------------------------------------------

    @GetMapping("/personnages/nouveau")
    public String afficherFormulaireCreationPersonnage(Model model) {
        model.addAttribute("personnage", new Personnage());
        return "formulaire_creation_personnage";
    }

    @PostMapping("/personnages/nouveau")
    public String creerPersonnage(@ModelAttribute("personnage") Personnage personnage,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "formulaire_creation_personnage";
        }

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/personnages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<Personnage> request = new HttpEntity<>(personnage, headers);
        ResponseEntity<Personnage> response = restTemplate.postForEntity(apiUrl, request, Personnage.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {

            Personnage nouveauPersonnage = response.getBody();

            personnages.add(nouveauPersonnage);
        }

        return "redirect:/personnages";
    }



//    @PostMapping("/personnages/nouveau")
//    public String creerPersonnage(@ModelAttribute("personnage") Personnage personnage,
//                                  BindingResult result) {
//        // Gérer la logique de création du personnage et l'ajouter à la liste
//
//        if (result.hasErrors()) {
//            return "formulaire_creation_personnage";
//        }
//
//        personnage.setId(++dernierId);
//        personnages.add(personnage);
//
//        return "redirect:/personnages";
//    }




    @GetMapping("/personnages/{id}")
    public String afficherFichePersonnage(@PathVariable("id") long id, Model model) {
        Personnage personnage = trouverPersonnageParId(id);
        model.addAttribute("personnage", personnage);
        return "fiche_personnage";
    }
    //---------------------------------------------------------------------------------
//    @GetMapping("/personnages/{id}/modifier")
//    public String afficherFormulaireModificationPersonnage(@PathVariable("id") long id, Model model) {
//        Personnage personnage = trouverPersonnageParId(id);
//        model.addAttribute("personnage", personnage);
//        return "formulaire_modification_personnage";
//    }

//    @PostMapping("/personnages/{id}/modifier")
//    public String modifierPersonnage(@PathVariable("id") long id,
//                                     @ModelAttribute("personnage") Personnage personnage,
//                                     BindingResult result) {
//
//        if (result.hasErrors()) {
//            return "formulaire_modification_personnage";
//        }
//
//        // Trouver le personnage à modifier par son ID
//        Personnage personnageExistant = trouverPersonnageParId(id);
//
//        // Mettre à jour les attributs du personnage existant avec les nouvelles valeurs
//        personnageExistant.setNom(personnage.getNom());
//        personnageExistant.setType(personnage.getType());
//        personnageExistant.setPointsDeVie(personnage.getPointsDeVie());
//
//        return "redirect:/personnages";
//    }


    @PostMapping("/personnages/{id}/modifier")
    public String modifierPersonnage(@PathVariable("id") long id,
                                     @ModelAttribute("personnage") Personnage personnage,
                                     BindingResult result) {

        if (result.hasErrors()) {
            return "formulaire_modification_personnage";
        }

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/personnages/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Personnage> request = new HttpEntity<>(personnage, headers);
        restTemplate.exchange(apiUrl, HttpMethod.PUT, request, Personnage.class);

        Personnage personnageExistant = trouverPersonnageParId(id);
        personnageExistant.setNom(personnage.getNom());
        personnageExistant.setType(personnage.getType());
        personnageExistant.setPointsDeVie(personnage.getPointsDeVie());

        return "redirect:/personnages";
    }

    @GetMapping("/personnages/{id}/modifier")
    public String afficherFormulaireModificationPersonnage(@PathVariable("id") long id, Model model) {
        Personnage personnage = trouverPersonnageParId(id);
        model.addAttribute("personnage", personnage);
        return "formulaire_modification_personnage";
    }






    //---------------------------------------------------------------------------------

    @GetMapping("/personnages/{id}/supprimer")
    public String supprimerPersonnage(@PathVariable("id") long id) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/personnages/" + id;

        restTemplate.delete(apiUrl);

        Personnage personnage = trouverPersonnageParId(id);
        personnages.remove(personnage);

        return "redirect:/personnages";
    }


//    @GetMapping("/personnages/{id}/supprimer")
//    public String supprimerPersonnage(@PathVariable("id") long id) {
//        Personnage personnage = trouverPersonnageParId(id);
//        personnages.remove(personnage);
//        return "redirect:/personnages";
//    }

    //---------------------------------------------------------------------------------


    private Personnage trouverPersonnageParId(long id) {
        for (Personnage personnage : personnages) {
            if (personnage.getId() == id) {
                return personnage;
            }
        }
        return null;
    }


}