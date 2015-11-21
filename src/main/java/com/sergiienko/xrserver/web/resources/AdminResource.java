package com.sergiienko.xrserver.web.resources;

import com.sergiienko.xrserver.AppState;
import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.abstracts.RatesParser;
import com.sergiienko.xrserver.models.CurrencyGroupModel;
import com.sergiienko.xrserver.models.GroupModel;
import com.sergiienko.xrserver.models.RateModel;
import com.sergiienko.xrserver.models.SourceModel;
import com.sergiienko.xrserver.rest.resources.RateResource;
import com.sergiienko.xrserver.rest.resources.ResRate;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashMap;

/**
 * Serves admin related REST API and HTML pages
 */
@Path("/")
public class AdminResource {
    /**
     * Link to the main admin web page
     */
    private static final String ADMIN_PAGE_LINK = "<a href=\"/admin\">Admin page</a>";

    /**
     * HTML header
     */
    private static final String HEADER = "<html><head>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"static/css/main.css\">"
            + "</head><body>";

    /**
     * HTML footer
     */
    private static final String FOOTER = "</body></html>";

    /**
     * Entity manager object, for working with DB
     */
    private EntityManager entityManager = EMF.ENTITY_MANAGER_FACTORY.createEntityManager();

    /**
     * Logger object, for writing logs
     */
    private Logger logger = LoggerFactory.getLogger(AdminResource.class);

    /**
        Default admin web page
        @return             default admin HTMl page
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public final String main() {
        entityManager.getTransaction().begin();
        List<SourceModel> sources = entityManager.createQuery("from SourceModel", SourceModel.class).getResultList();
        List<GroupModel> groups = entityManager.createQuery("from GroupModel", GroupModel.class).getResultList();
        List<CurrencyGroupModel> currencyGroups = entityManager.createQuery("from CurrencyGroupModel", CurrencyGroupModel.class).getResultList();
        entityManager.getTransaction().commit();

        StringBuilder strSources = new StringBuilder();
        for (SourceModel source : sources) {
            strSources.append("<tr><td><a href=\"sources/" + source.getId() + "\">" + source.getId() + "</a>"
                    + "</td><td>" + source.getName()
                    + "</td><td>" + source.getUrl()
                    + "</td><td>" + source.getDescr()
                    + "</td><td>" + source.getParserClassName()
                    + "</td><td>" + source.getEnabled()
                    + "</td></tr>");
        }

        StringBuilder strGroups = new StringBuilder();
        for (GroupModel group : groups) {
            strGroups.append("<tr><td><a href=\"groups/" + group.getId() + "\">" + group.getId() + "</a>"
                    + "</td><td>" + group.getName()
                    + "</td><td>" + group.getDescr()
                    + "</td><td>" + Arrays.toString(group.getSources())
                    + "</td><td>" + group.getDefaultGroup()
                    + "</td></tr>");
        }

        StringBuilder strCurrencyGroups = new StringBuilder();
        for (CurrencyGroupModel currencyGroup : currencyGroups) {
            strCurrencyGroups.append("<tr><td><a href=\"currencygroups/" + currencyGroup.getId() + "\">"
                    + currencyGroup.getId() + "</a>"
                    + "</td><td>" + currencyGroup.getName()
                    + "</td><td>" + currencyGroup.getDescr()
                    + "</td><td>" + currencyGroup.getDefaultGroup()
                    + "</td></tr>");
        }

        String newSources = "<form action=\"sources/new\" method=\"post\">"
                + "<input type=\"text\" placeholder=\"Name\" name=\"name\">"
                + "<input type=\"text\" placeholder=\"URL\" name=\"url\">"
                + "<input type=\"text\" placeholder=\"Description\" name=\"descr\">"
                + getParsersHTMLClassNames()
                + "<input type=\"submit\"></form>";

        String newGroup = "<form action=\"groups/new\" method=\"post\">"
                + "<input type=\"text\" placeholder=\"Name\" name=\"name\">"
                + "<input type=\"text\" placeholder=\"Description\" name=\"descr\">"
                + "<input type=\"submit\"></form>";

        String newCurrencyGroup = "<form action=\"currencygroups/new\" method=\"post\">"
                + "<input type=\"text\" placeholder=\"Name\" name=\"name\">"
                + "<input type=\"text\" placeholder=\"Description\" name=\"descr\">"
                + "<input type=\"submit\"></form>";

        return HEADER
                + "<h3>Sources</h3><table style=\"width:100%\"><tr><th>ID</th><th>Name</th>"
                + "<th>URL</th><th>Description</th><th>Parser</th><th>Enabled</th></tr>"
                + strSources + "</table><br><hr><br>"
                + "<h3>Add new source</h3><strong>all fields are mandatory</strong>"
                + newSources + "<br><hr><br>"
                + "<h3>Groups</h3><table style=\"width:100%\"><tr><th>ID</th><th>Name</th><th>Description</th><th>Sources</th><th>Default</th></tr>"
                + strGroups + "</table><br><hr><br>"
                + "<h3>Add new group</h3><strong>all fields are mandatory</strong>"
                + newGroup
                + "<br><hr><br>"
                + "<h3>Currency Groups</h3><table style=\"width:100%\"><tr><th>ID</th><th>Name</th><th>Description</th><th>Default</th></tr>"
                + strCurrencyGroups
                + "</table><br><hr><br>"
                + "<h3>Add new currency group</h3><strong>all fields are mandatory</strong>"
                + newCurrencyGroup + "<br><hr><br>"
                + FOOTER;
    }

    /**
        Returns known parsers' full class names in HTML string
        @return             a select HTML element with list of known sources' parsers
     */
    private String getParsersHTMLClassNames() {
        StringBuilder parsers = new StringBuilder("<select name=\"parserclass\" id=\"parserclass\">");
        Reflections reflections = new Reflections("com.sergiienko.xrserver.parsers");
        Set<Class<? extends RatesParser>> allClasses = reflections.getSubTypesOf(RatesParser.class);
        for (Class c : allClasses) {
            String className = c.getName();
            parsers.append("<option value=\"" + className + "\">" + className + "</option>");
        }
        parsers.append("</select>");
        return parsers.toString();
    }

    /**
        Show group edit page
        @param groupid          ID of the group
        @return                 a web page for editing the group's properties
     */
    @GET
    @Path("/groups/{groupid}")
    @Produces(MediaType.TEXT_HTML)
    public final String editGroup(@PathParam("groupid") final Integer groupid) {
        StringBuilder sb = new StringBuilder(HEADER);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class);
        q.setParameter("arg1", groupid);
        GroupModel group = (GroupModel) q.getSingleResult();
        List<SourceModel> sources = entityManager.createQuery("from SourceModel", SourceModel.class).getResultList();
        entityManager.getTransaction().commit();
        sb.append("<form action=\"" + groupid + "\" method=\"post\">");
        sb.append("Name <input name=\"name\" value=\"" + group.getName() + "\"><br>");
        sb.append("Description <input name=\"descr\" value=\"" + group.getDescr() + "\"><br>");
        sb.append("Default group <input type=\"checkbox\" name=\"default\" " + (group.getDefaultGroup() ? "checked=\"true\"><br>" : "><br>"));
        sb.append("Sources in group<br>");
        List<Integer> l = Arrays.asList(group.getSources());
        for (SourceModel source: sources) {
            String checked = l.contains(source.getId()) ? " checked=\"true\" " : "";
            sb.append("<input type=\"checkbox\" name=\"source\" " + checked + " value=\"" + source.getId() + "\">" + source.getName() + "<br>");
        }
        sb.append("<input type=\"submit\"></form>");
        sb.append("<br><a href=\"" + groupid + "/remove\">Delete group</a></br>");
        return sb.toString();
    }

    /**
        Remove group from DB
        @param groupid          ID of the group
        @return                 a link on the admin web page
    */
    @GET
    @Path("/groups/{groupid}/remove")
    @Produces(MediaType.TEXT_HTML)
    public final String removeGroup(@PathParam("groupid") final Integer groupid) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("delete GroupModel where id=:arg1");
        q.setParameter("arg1", groupid);
        q.executeUpdate();
        entityManager.getTransaction().commit();
        logger.info("Group " + groupid + " has been removed");
        return "<br>Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Remove source from DB
        @param sourceid         ID of the source
        @return                 a link to the admin web page
     */
    @GET
    @Path("/sources/{sourceid}/remove")
    @Produces(MediaType.TEXT_HTML)
    public final String removeSource(@PathParam("sourceid") final Integer sourceid) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from RateModel where source=:arg1", RateModel.class);
        q.setParameter("arg1", sourceid);
        q.setMaxResults(1);
        List<Object> l = q.getResultList();
        String res;
        if (0 == l.size()) {
            q = entityManager.createQuery("delete SourceModel where id=:arg1");
            q.setParameter("arg1", sourceid);
            q.executeUpdate();
            res = "Success.";
        } else {
            res = "We have rates from this source in DB. Cannot be removed.";
        }
        entityManager.getTransaction().commit();
        logger.info("Source " + sourceid + " has been removed");
        return res + "<br>Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Show source edit page
        @param sourceid         ID of the source
        @return                 web page for editing the surce's properties
    */
    @GET
    @Path("/sources/{sourceid}")
    @Produces(MediaType.TEXT_HTML)
    public final String editSource(@PathParam("sourceid") final Integer sourceid) {
        StringBuilder sb = new StringBuilder(HEADER);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from SourceModel where id=:arg1", SourceModel.class);
        q.setParameter("arg1", sourceid);
        SourceModel source = (SourceModel) q.getSingleResult();
        entityManager.getTransaction().commit();
        sb.append("<form action=\"" + sourceid + "\" method=\"post\">");
        sb.append("Name <input name=\"name\" value=\"" + source.getName() + "\"><br>");
        sb.append("Description <input name=\"descr\" value=\"" + source.getDescr() + "\"><br>");
        sb.append("URL <input name=\"url\" value=\"" + source.getUrl() + "\"><br>");
        sb.append("Parser " + getParsersHTMLClassNames() + "<br>");
        sb.append("Enabled <input type=\"checkbox\" name=\"enabled\" " + (source.getEnabled() ? "checked=\"true\"><br>" : "><br>"));
        sb.append("Sources in group<br>");
        sb.append("<input type=\"submit\"></form>");
        sb.append("<br><a href=\"" + sourceid + "/remove\">Delete source</a> - can be deleted if no rates from this source are in DB</br>");
        sb.append("<script>var el = document.getElementById(\'parserclass\');\nel.value=\""
                + source.getParserClassName() + "\"</script>");
        return sb.toString();
    }

    /**
        Persist group after edit
        @param name         name of the group
        @param descr        description of the group
        @param dflt         is the group default
        @param groupid      ID of the group
        @param sources      list of sources arranged in this group
        @return             a link on the admin web page
     */
    @POST
    @Path("/groups/{groupid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String saveGroup(@PathParam("groupid") final Integer groupid,
                            @FormParam("source") final List<Integer> sources,
                            @FormParam("name") final String name,
                            @FormParam("descr") final String descr,
                            @FormParam("default") final Boolean dflt) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class);
        q.setParameter("arg1", groupid);
        GroupModel group = (GroupModel) q.getSingleResult();
        group.setSources(sources.toArray(new Integer[sources.size()]));
        group.setDescr(descr);
        group.setName(name);
        if (null != dflt) {
            entityManager.createQuery("UPDATE GroupModel SET dflt=:state WHERE id<>:groupid").
                    setParameter("state", false).setParameter("groupid", groupid).
                    executeUpdate();
        }
        group.setDefaultGroup(null == dflt ? false : true);
        entityManager.merge(group);
        entityManager.getTransaction().commit();
        logger.info("Group " + groupid + "has been edited");
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Persist source after edit
        @param groupid          ID of the source
        @param enabled          is the source enabled
        @param descr            description of the source
        @param name             name of the source
        @param parser           class name of the parser which should serve this source
        @param url              URL of the source
        @return                 a link to the admin web page
     */
    @POST
    @Path("/sources/{sourceid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String saveSource(@PathParam("sourceid") final Integer groupid,
                            @FormParam("url") final String url,
                            @FormParam("name") final String name,
                            @FormParam("descr") final String descr,
                            @FormParam("parserclass") final String parser,
                            @FormParam("enabled") final Boolean enabled) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from SourceModel where id=:arg1", SourceModel.class);
        q.setParameter("arg1", groupid);
        SourceModel source = (SourceModel) q.getSingleResult();
        source.setParserClassName(parser);
        source.setUrl(url);
        source.setDescr(descr);
        source.setName(name);
        source.setEnabled((null == enabled ? false : true));
        entityManager.merge(source);
        entityManager.getTransaction().commit();
        logger.info("Source " + groupid + "has been edited");
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Create new source
        @param name         name of the source
        @param url          URL of the source
        @param descr        description of the source
        @param parserclass  class name of the parser which serves this source
        @return             a link to the man admin web page
     */
    @POST
    @Path("/sources/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String createSource(@FormParam("name") final String name,
                                     @FormParam("url") final String url,
                                     @FormParam("descr") final String descr,
                                     @FormParam("parserclass") final String parserclass) {
        if (null == name || 0 == name.length()
            || null == url || 0 == url.length()
            || null == descr || 0 == descr.length()
            || null == parserclass || 0 == parserclass.length()) {
            return "All form fields are mandatory. Return to " + ADMIN_PAGE_LINK;
        }
        SourceModel newsource = new SourceModel();
        newsource.setName(name);
        newsource.setDescr(descr);
        newsource.setEnabled(false);
        newsource.setParserClassName(parserclass);
        newsource.setUrl(url);
        entityManager.getTransaction().begin();
        entityManager.persist(newsource);
        entityManager.getTransaction().commit();
        logger.info("New source added: " + url);
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Create new group
        @param name         name of the group
        @param descr        description of the group
        @return             a link to the main admin page
     */
    @POST
    @Path("/groups/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String createGroup(@FormParam("name") final String name, @FormParam("descr") final String descr) {
        if (null == name || 0 == name.length() || null == descr || 0 == descr.length()) {
            return "All form fields are mandatory. Return to " + ADMIN_PAGE_LINK;
        }
        GroupModel newgroup = new GroupModel();
        newgroup.setName(name);
        newgroup.setDescr(descr);
        newgroup.setDefaultGroup(false);
        newgroup.setSources(new Integer[]{});
        entityManager.getTransaction().begin();
        entityManager.persist(newgroup);
        entityManager.getTransaction().commit();
        logger.info("New group added: " + name);
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /**
        Get application state
        @return         last state of enabled sources, application/json
    */
    @GET
    @Path("/rest/state")
    @Produces(MediaType.APPLICATION_JSON)
    public final String getState() {
        StringBuilder sb = new StringBuilder("{state: {sources: {");
        Map<Integer, Boolean> state = AppState.getState();
        int i = 0;
        for (Map.Entry<Integer, Boolean> entry : state.entrySet()) {
            if (i > 0) {
                sb.append(",");
            } else {
                i++;
            }
            sb.append("\"" + entry.getKey() + "\":" + (entry.getValue() ? "\"OK\"" : "\"ERROR\""));
        }
        sb.append("}}}");
        return sb.toString();
    }

    /**
        Get state for specific source
        @param sourceid    ID of the source we want get state of
        @return            last state of the source, text/html
    */
    @GET
    @Path("/rest/state/{sourceid}")
    @Produces(MediaType.TEXT_HTML)
    public final String getStateID(@PathParam("sourceid") final Integer sourceid) {
        StringBuilder sb = new StringBuilder("<html><body>");
        Map<Integer, Boolean> state = AppState.getState();
        Boolean sourceState = state.get(sourceid);
        if (null == sourceState) {
            sb.append("NO STATE");
        } else {
            sb.append(sourceState ? "OK" : "ERROR");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    /**
     Show currency group edit page
     @param groupid ID of the currency group
     @return a web page for editing the group's properties
     */
    @GET
    @Path("/currencygroups/{groupid}")
    @Produces(MediaType.TEXT_HTML)
    public final String editCurrencyGroup(@PathParam("groupid") final Integer groupid) {
        StringBuilder sb = new StringBuilder(HEADER);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from CurrencyGroupModel where id=:arg1", CurrencyGroupModel.class);
        q.setParameter("arg1", groupid);
        CurrencyGroupModel group = (CurrencyGroupModel) q.getSingleResult();
        entityManager.getTransaction().commit();
        sb.append("<form action=\"" + groupid + "\" method=\"post\">");
        sb.append("Name <input name=\"name\" value=\"" + group.getName() + "\"><br>");
        sb.append("Description <input name=\"descr\" value=\"" + group.getDescr() + "\"><br>");
        sb.append("Default group <input type=\"checkbox\" name=\"default\" " + (group.getDefaultGroup() ? "checked=\"true\"><br>" : "><br>"));
        sb.append("Sources in group<br>");
        Integer[] groupSources = group.getSources();
        String[] groupCurrencies = group.getCurrencies();
        RateResource rateRes = new RateResource();
        List<ResRate> ratesList = rateRes.getRatesForSourceID(null, null, null);
        Map<Integer, List<ResRate>> ratesMap = new HashMap<>();
        for (ResRate r : ratesList) {
            if (null == ratesMap.get(r.getSource())) {
                ratesMap.put(r.getSource(), new ArrayList<ResRate>());
            }
            ratesMap.get(r.getSource()).add(r);
        }
        sb.append("<table><tr><th></th><th>Source ID</th><th>Currency name</th></tr>");
        for (Map.Entry<Integer, List<ResRate>> entry : ratesMap.entrySet()) {
            Integer sourceID = entry.getKey();
            for (ResRate rate : entry.getValue()) {
                String currencyName = rate.getName();
                String checked = "";
                for (int i = 0; i < groupSources.length; i++) {
                    if (groupSources[i].equals(sourceID) && currencyName.equals(groupCurrencies[i])) {
                        checked = " checked=\"true\" ";
                        break;
                    }
                }
                String checkboxValue = sourceID + ":" + currencyName;
                sb.append("<tr><td><input type=\"checkbox\" name=\"source\" " + checked + " value=\"" + checkboxValue + "\"></td>"
                        + "<td>" + sourceID + "</td><td>" + currencyName + "</td></tr>");
            }
        }
        sb.append("</table><input type=\"submit\"></form>");
        sb.append("<br><a href=\"" + groupid + "/remove\">Delete group</a></br>");
        return sb.toString();
    }

    /**
     Persist currency group after edit
     @param name         name of the group
     @param descr        description of the group
     @param dflt         is the group default
     @param groupid      ID of the group
     @param sources      list of pairs 'source:value' arranged in this group
     @return             a link on the admin web page
     */
    @POST
    @Path("/currencygroups/{groupid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String saveCurrencyGroup(@PathParam("groupid") final Integer groupid,
                                  @FormParam("source") final List<String> sources,
                                  @FormParam("name") final String name,
                                  @FormParam("descr") final String descr,
                                  @FormParam("default") final Boolean dflt) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from CurrencyGroupModel where id=:arg1", CurrencyGroupModel.class);
        q.setParameter("arg1", groupid);
        CurrencyGroupModel group = (CurrencyGroupModel) q.getSingleResult();
        List<Integer> formSources = new ArrayList<>();
        List<String> formCurrencies = new ArrayList<>();
        for (String s : sources) {
            String[] parts = s.split(":");
            formSources.add(Integer.parseInt(parts[0]));
            formCurrencies.add(parts[1]);
        }
        group.setSources(formSources.toArray(new Integer[formSources.size()]));
        group.setCurrencies(formCurrencies.toArray(new String[formCurrencies.size()]));
        group.setDescr(descr);
        group.setName(name);
        if (null != dflt) {
            entityManager.createQuery("UPDATE CurrencyGroupModel SET dflt=:state WHERE id<>:groupid").
                    setParameter("state", false).setParameter("groupid", groupid).
                    executeUpdate();
        }
        group.setDefaultGroup(null == dflt ? false : true);
        entityManager.merge(group);
        entityManager.getTransaction().commit();
        logger.info("Currency group " + groupid + "has been edited");
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /**
     Remove currency group from DB
     @param groupid ID of the group
     @return a link on the admin web page
     */
    @GET
    @Path("/currencygroups/{groupid}/remove")
    @Produces(MediaType.TEXT_HTML)
    public final String removeCurrencyGroup(@PathParam("groupid") final Integer groupid) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("delete CurrencyGroupModel where id=:arg1");
        q.setParameter("arg1", groupid);
        q.executeUpdate();
        entityManager.getTransaction().commit();
        logger.info("Currency group " + groupid + " has been removed");
        return "<br>Return to " + ADMIN_PAGE_LINK;
    }

    /**
     Create empty currency group
     @param name name of the group
     @param descr description of the group
     @return a link to the main admin page
     */
    @POST
    @Path("/currencygroups/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public final String createCurrencyGroup(@FormParam("name") final String name,
                                            @FormParam("descr") final String descr) {
        if (null == name || 0 == name.length() || null == descr || 0 == descr.length()) {
            return "All form fields are mandatory. Return to " + ADMIN_PAGE_LINK;
        }
        CurrencyGroupModel newgroup = new CurrencyGroupModel();
        newgroup.setName(name);
        newgroup.setDescr(descr);
        newgroup.setDefaultGroup(false);
        newgroup.setSources(new Integer[]{});
        newgroup.setCurrencies(new String[]{});
        entityManager.getTransaction().begin();
        entityManager.persist(newgroup);
        entityManager.getTransaction().commit();
        logger.info("New currency group added: " + name);
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }
}
