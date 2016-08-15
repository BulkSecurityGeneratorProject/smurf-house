package com.smurfhouse.scratch.scratch;

import com.smurfhouse.scratch.model.PageParsed;
import com.smurfhouse.scratch.model.ScratchHouse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.smurfhouse.scratch.util.UtilScratch.*;

/**
 * Created by fmunozse on 6/7/16.
 */
public class IdealistaScratch {

    public static final String REGEXP_PRICE = "^(.*) €$";

    public static final String REGEXP_METERS = "^(.*) m²$";

    public static final String REGEXP_ROOMS = "^(.*) hab.$";

    public static final String REGEXP_FLOOR_PLANTA = "^(.*)ª planta.*";

    public static final String REGEXP_FLOOR_BAJA = "^(Bajo).*";

    public static final String REGEXP_MATCH_ELEVATOR = ".*con ascensor.*";

    public static final String REGEXP_MATCH_FACING_OUTSIDE = ".*exterior.*";

    public static final String REGEXP_MATCH_GARAGE = ".*Garaje incluido.*";

    public PageParsed parsePage (Document doc, Locale locale) {
        if (doc == null || locale == null) {
            return new PageParsed (new LinkedList<ScratchHouse>(), null);
        }

        List<ScratchHouse> lHouses = new LinkedList<>();

        Elements elements = doc.select("article");
        for (Element e: elements) {
            ScratchHouse h = parseHouse(e, locale);
            if (h != null) {
                lHouses.add(h);
            }
        }
        return new PageParsed (lHouses, getNext(doc));
    }

    private String getNext(Document doc) {
        Elements links = doc.select(".icon-arrow-right-after");
        if (links.size()>0) {
            return links.first().attr("href");
        }
        return null;
    }


    private ScratchHouse parseHouse(Element element, Locale locale) {
        ScratchHouse house = new ScratchHouse();

        String id = findAttributeElementSafeNullPointer(element, ".item-link", "href");
        if (id == null || id.isEmpty()) {
            return null;
        }

        house.setId(id);
        house.setTitle(findTextElementSafeNullPointer(element, ".item-link"));
        house.setPrice(
                findBigDecimalFirstGroupByRegExpSafeNullPointer(
                        findTextElementSafeNullPointer(element, ".item-price"),
                        REGEXP_PRICE,
                        locale
                )
        );
        house.setDetails(element.select(".item-detail").stream()
                .map(e -> {
                    String text = e.text();

                    Integer i = findIntegerFirstGroupByRegExpSafeNullPointer(text, REGEXP_METERS, locale);
                    if (i != null) house.setMeters(i);

                    i = findIntegerFirstGroupByRegExpSafeNullPointer(text, REGEXP_ROOMS, locale);
                    if (i != null) house.setNumberRooms(i);

                    String s = findTextFirstGroupByRegExpSafeNullPointer(text, REGEXP_FLOOR_PLANTA);
                    if (s != null) house.setFloor(s);
                    s = findTextFirstGroupByRegExpSafeNullPointer(text, REGEXP_FLOOR_BAJA);
                    if (s != null) house.setFloor(s);

                    Boolean match = matchTextByRegExpSafeNullPointer(text, REGEXP_MATCH_ELEVATOR);
                    if (Boolean.TRUE.equals(match)) house.setHasElevator(match);

                    match = matchTextByRegExpSafeNullPointer(text, REGEXP_MATCH_FACING_OUTSIDE);
                    if (Boolean.TRUE.equals(match)) house.setFacingOutside(match);

                    match = matchTextByRegExpSafeNullPointer(text, REGEXP_MATCH_GARAGE);
                    if (Boolean.TRUE.equals(match)) house.setHasGarage(match);

                    return text;
                })
                .collect(Collectors.joining(" ")));

        return house;
    }
}

