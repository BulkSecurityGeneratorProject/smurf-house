package com.smurfhouse.scratch.scratch;

import com.smurfhouse.scratch.model.PageParsed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by fmunozse on 9/7/16.
 */
public class IdealistaScratchTest {

    public static final String HTML_IDEALISTA_SEARCH_1 = "<article> <div class=\"item\" data-adid=\"32835822\"> <div class=\"clearfix\" style=\"display: block;\"> <div class=\"item-multimedia \"> <div class=\"item-multimedia-features\"> <span class=\"icon-virtual-tour\"></span> <span class=\"icon-plans\"></span> </div> <div class=\"item-multimedia-pictures\"> <span class=\"icon-photos\"></span> <span data-count=\"\">1/</span><span data-total=\"\">30</span> </div> <div class=\"item-ribbon-container\"> </div> <span class=\"item-push-up\">Destacado</span> <div class=\"item-gallery\"><div class=\"mask-wrapper is-clickable\"><div class=\"gallery-arrow left icon-arrow-left\" style=\"visibility: hidden;\"></div><div class=\"gallery-arrow right icon-arrow-right\" style=\"visibility: visible;\"></div><div class=\"mask galleryBoost\" style=\"touch-action: pan-y; -webkit-user-select: none; -webkit-user-drag: none; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); transition-duration: 0s; transform: translate3d(0px, 0px, 0px); width: 580px;\"><div class=\"placeholder\" style=\"transform: translateX(-580px);\"></div><div class=\"placeholder\" style=\"transform: translateX(0px);\"><img src=\"https://img3.idealista.com/thumbs?wi=300&amp;he=225&amp;en=%2BtSLyO%2BcnvWFQ1vfQ1%2FQREouafxFc5W9bn7k7herWZpUQKJjQ6cy1mJiIGePyanwenbPKfuOPIaCEFuXJKqGB5cU1VWesisrLJUHqccKZOukuYN5VRYLz1RLlDEtglEcKFetA5LMd2SHdJFqqai0nA%3D%3D&amp;ch=-1988943871\" class=\"vertical\" style=\"visibility: visible;\"></div><div class=\"placeholder\" style=\"transform: translateX(580px);\"><img src=\"https://img3.idealista.com/thumbs?wi=300&amp;he=225&amp;en=%2BtSLyO%2BcnvWFQ1vfQ1%2FQRLdajt2vNupSvEHNehjedzBsbki%2Fnb5o6bIjU4wZt4lBenbPKfuOPIaCEFuXJKqGB5cU1VWesisrLJUHqccKZOukuYN5VRYLz1RLlDEtglEcKFetA5LMd2SHdJFqqai0nA%3D%3D&amp;ch=-805751556\" class=\"vertical\" style=\"visibility: visible;\"></div></div></div></div> </div> <div class=\"item-info-container\"> <a href=\"/inmueble/32835822/\" class=\"item-link \" title=\"Piso en calle de arturo soria, Colina, Madrid\" data-xiti-click=\"listado::enlace\">Piso en calle de arturo soria, Colina, Madrid</a> <div class=\"row price-row clearfix\"> <span class=\"item-price\">871.000<span>€</span></span> <span>Garaje incluido</span> </div> <span class=\"item-detail\">5 <small>hab.</small></span> <span class=\"item-detail\">247 <small>m²</small></span> <span class=\"item-detail\">2ª <small><span>planta</span> exterior con ascensor</small></span> <p class=\"item-description\">EXTRA Inmobiliaria vende vivienda exclusiva y señorial en la urbanización Los Cedros. Destacamos el salón de 50 m2 con chimenea y salida...</p> <div class=\"item-toolbar clearfix\"> <div class=\"item-toolbar-contact\"> <span class=\"icon-phone item-not-clickable-phone\">651 360 158</span> <a class=\"icon-phone phone-btn item-clickable-phone\" target=\"_blank\" href=\"tel:651360158\" data-xiti-markup=\"{ &quot;click&quot;: {&quot;xtPage&quot;:&quot;listado::conversiones::contacto-por-telf&quot;,&quot;mustXtn2&quot;:true,&quot;actionType&quot;:&quot;PAGE&quot;} }\" data-xiti-page=\"telf_cliente_v\"> <span>651 360 158</span> </a> <button class=\"icon-mail email-btn action-email fake-anchor\" data-xiti-click=\"listado::enlace-contactar\"><span>Contactar</span></button> </div> <div class=\"item-toolbar-actions\"> <button class=\"icon-fav favorite-btn action-fav fake-anchor\" data-role=\"add\" data-text-add=\"Guardar\" data-text-remove=\"Quitar\"> <span>Guardar</span> </button> <button class=\"icon-delete trash-btn action-discard fake-anchor\" data-role=\"add\" data-text-remove=\"Descartar\" rel=\"nofollow\"><span>Descartar</span></button> </div> </div> </div> </div> </div> </article>";
    public static final String HTML_IDEALISTA_SEARCH_2 = "<div class=\"pagination\"> <ul> <li class=\"moreresults\"><span>Más resultados:</span></li><li class=\"selected\"><span>1</span></li><li><a class=\"\" href=\"/venta-viviendas/madrid/ciudad-lineal/colina/pagina-2.htm\">2</a></li><li><a class=\"\" href=\"/venta-viviendas/madrid/ciudad-lineal/colina/pagina-3.htm\">3</a></li><li><a class=\"\" href=\"/venta-viviendas/madrid/ciudad-lineal/colina/pagina-4.htm\">4</a></li><li class=\"next\"><a class=\"icon-arrow-right-after\" href=\"/venta-viviendas/madrid/ciudad-lineal/colina/pagina-2.htm\"><span>Siguiente</span></a></li> </ul> </div>";

    @Test
    public void parsePage() throws Exception {
        IdealistaScratch idealistaScratch = new IdealistaScratch();

        Document doc = Jsoup.parse(HTML_IDEALISTA_SEARCH_1+HTML_IDEALISTA_SEARCH_2);

        PageParsed pargeParsed = idealistaScratch.parsePage(doc, Locale.getDefault());
        assertNotNull (pargeParsed);
        assertNotNull(pargeParsed.getNextUrl());
        assertNotNull(pargeParsed.getHousesCurrentPage());
        assertTrue(pargeParsed.getHousesCurrentPage().size() == 1);
        assertEquals(pargeParsed.getHousesCurrentPage().get(0).getDetails() , "5 hab. 247 m² 2ª planta exterior con ascensor");

    }

}
