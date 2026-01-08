package org.jluc.ctr.tools.calendrier.server.service.google.calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jboss.logging.Logger;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeActivite;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.common.io.BaseEncoding;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarServices {

    // Listes des calendriers :
    // Calendrier - 1 - Numéros de semaine
    // Calendrier - 2 - Jours fériés en France
    // Calendrier - 3 - Module 6 - 20
    // Calendrier - 4 - ANTEOR
    // Calendrier - 5 - GP N4 CTR BPL
    // Calendrier - 6 - Handi
    // Calendrier - 7 - INITIATEUR CTR BPL
    // Calendrier - 8 - MF1 CTR BPL
    // Calendrier - 9 - MF2 CTR BPL
    // Calendrier - 10 - Module 20-40
    // Calendrier - 11 - RIFA
    // Calendrier - 12 - TEK
    // Calendrier - 13 - TIV CTR BPL
    // Calendrier - 14 - TSI
    // Calendrier - 15 - Recycleur BPL
    // Calendrier - 16 - planning.ctrbpl@gmail.com
    private static CalendarServices mInstance;

    private Map<TypeCalendar, CalendarListEntry> mCalendarList;

    private Logger mLogger = Logger.getLogger(CalendarServices.class);

    /**
     * Application name.
     */
    // private static final String APPLICATION_NAME = "CalendrierCTR";
    private static final String APPLICATION_NAME = "calendrierctr";
    /**
     * Global instance of the JSON factory.
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying
     * these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Calendar mService;

    public static CalendarServices getInstance() {
        if (mInstance == null) {
            mInstance = new CalendarServices();
        }
        return mInstance;
    }

    private CalendarServices() {
        // Build a new authorized API client service.
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            mService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME).build();
            CalendarList calendarList = mService.calendarList().list().execute();
            mCalendarList = new HashMap<TypeCalendar, CalendarListEntry>();
            int i = 1;
            for (CalendarListEntry calendar : calendarList.getItems()) {
                System.out.println("Calendrier - " + i + " - " + calendar.getSummary());
                // Ici il faut stocker les calendrier en fonction des activites
                String summary = calendar.getSummary().trim();
                if (summary.equalsIgnoreCase(TypeCalendar.CTR.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.CTR, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.TIV.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.TIV, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.RECYCLEUR.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.RECYCLEUR, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.MF2.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.MF2, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.MODULE_20_40.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.MODULE_20_40, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.ANTEOR.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.ANTEOR, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.GP_N4.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.GP_N4, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.RIFA.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.RIFA, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.TSI.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.TSI, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.HANDI.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.HANDI, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.MF1.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.MF1, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.MODULE_6_20.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.MODULE_6_20, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.INITIATEUR.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.INITIATEUR, calendar);
                } else if (summary.equalsIgnoreCase(TypeCalendar.TEK.getCalendarName())) {
                    mCalendarList.put(TypeCalendar.TEK, calendar);
                }
                i++;
            }
            // List the next 10 events from the primary calendar.
            Events events = mService.events().list(mCalendarList.get(TypeCalendar.MODULE_6_20).getId())
                    .setMaxResults(10)
                    .setOrderBy("startTime").setSingleEvents(true).execute();
            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                System.out.println("No upcoming events found.");
            } else {
                System.out.println("Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    System.out.printf("%s (%s)\n ==> id %s\n", event.getSummary(), start, event.getId());
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            mLogger.error("Problem during calendar retrieving", e);
        }
        mLogger.debug(
                "Nb de calendriers dans le calendar : " + mCalendarList.size() + " / " + TypeCalendar.values().length);
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT
     *                       The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException
     *                     If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CalendarServices.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("planning.ctrbpl@gmail.com");
        // returns an authorized Credential object.
        return credential;
    }

    public boolean addEvent(Evenement event) throws IOException {
        TypeActivite activite = event.getType().getActivite();
        String typeEvenement = event.getType().getName();
        String description = "Organisateur : " + event.getDemandeur().getName() + "\n" + "\r\n"
                + "Contact : presidente-technique@cibpl.fr\r\n" + event.getMailcontact();

        String calendarId = mCalendarList.get(TypeCalendar.CTR).getId();

        switch (activite) {
            case ALL:
                break;
            case N4_GP:
                calendarId = mCalendarList.get(TypeCalendar.GP_N4).getId();
                break;
            case INITIATEUR:
                if (typeEvenement.equalsIgnoreCase("Module 6-20"))
                    calendarId = mCalendarList.get(TypeCalendar.MODULE_6_20).getId();
                else if (typeEvenement.equalsIgnoreCase("Module 20-40"))
                    calendarId = mCalendarList.get(TypeCalendar.MODULE_20_40).getId();
                else
                    calendarId = mCalendarList.get(TypeCalendar.INITIATEUR).getId();
                break;
            case TSI:
                calendarId = mCalendarList.get(TypeCalendar.INITIATEUR).getId();
                break;
            case MF1:
                calendarId = mCalendarList.get(TypeCalendar.MF1).getId();
                break;
            case MF2:
                calendarId = mCalendarList.get(TypeCalendar.MF2).getId();
                break;
            case TIV:
                calendarId = mCalendarList.get(TypeCalendar.TIV).getId();
                break;
            case SECOURISME:
                if (typeEvenement.equalsIgnoreCase("ANTEOR"))
                    calendarId = mCalendarList.get(TypeCalendar.ANTEOR).getId();
                else
                    calendarId = mCalendarList.get(TypeCalendar.RIFA).getId();
                break;
            case HANDISUB:
                calendarId = mCalendarList.get(TypeCalendar.HANDI).getId();
                break;
            default:
                break;

        }

        // On cherche si l'event existe deja
        if (event.getCalendareventid() != null
                && mService.events().get(calendarId, event.getCalendareventid()) != null) {
            // On a bien un event existant
            // Il faut le mettre à jour
            mLogger.debug("Event existant...");
            return false;
        } else {
            mLogger.debug("Event a creer...");
            Event eventCalendar = createEvent(typeEvenement + " - " + event.getDemandeur().getName(),
                    event.getLieu(),
                    description, event.getDatedebut(), event.getDatefin());

            mService.events().insert(calendarId, eventCalendar).execute();
            event.setCalendareventid(eventCalendar.getId());
        }
        return true;
    }

    private Event createEvent(String titre, String location, String description, Date startDate,
            Date endDate) {
        Event event = new Event().setSummary(titre).setLocation(location).setDescription(description);

        DateTime startDateTime = new DateTime(true, startDate.getTime() + (24 * 3600000), 120);
        EventDateTime start = new EventDateTime().setDate(startDateTime).setTimeZone("Europe/Paris");
        event.setStart(start);

        DateTime endDateTime = new DateTime(true, endDate.getTime() + (48 * 3600000), 120);
        EventDateTime end = new EventDateTime().setDate(endDateTime).setTimeZone("Europe/Paris");
        event.setEnd(end);

        String id = toBase32(UUID.randomUUID());
        event.setId(id);
        mLogger.debug("Id de l'event : " + id);

        return event;
    }

    public static String toBase32(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return BaseEncoding.base32().omitPadding().encode(bb.array()).toLowerCase().replace('w', 'v').replace('x', 'd')
                .replace('y', '0').replace('z', '6');
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        CalendarServices.getInstance();
    }
}
