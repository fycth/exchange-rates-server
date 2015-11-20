package com.sergiienko.xrserver.web.resources;

import com.sergiienko.xrserver.AppState;
import com.sergiienko.xrserver.EMF;
import com.sergiienko.xrserver.abstracts.RatesParser;
import com.sergiienko.xrserver.models.GroupModel;
import com.sergiienko.xrserver.models.RateModel;
import com.sergiienko.xrserver.models.SourceModel;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/")
public class AdminResource {
    private static final String ADMIN_PAGE_LINK = "<a href=\"/admin\">Admin page</a>";
    private static final String header = "<html>" +
            "<head>" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"static/css/main.css\">" +
            "</head><body>";
    private static final String footer = "</body></html>";

    EntityManager entityManager = EMF.entityManagerFactory.createEntityManager();
    Logger logger = LoggerFactory.getLogger(AdminResource.class);

    /*
    Default admin web page
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String main() {
        entityManager.getTransaction().begin();
        List<SourceModel> sources = entityManager.createQuery("from SourceModel", SourceModel.class).getResultList();
        List<GroupModel> groups = entityManager.createQuery("from GroupModel", GroupModel.class).getResultList();
        entityManager.getTransaction().commit();

        StringBuilder strSources = new StringBuilder();
        for (SourceModel source : sources) {
            strSources.append("<tr><td><a href=\"sources/" + source.getId() + "\">" + source.getId() + "</a>" +
                    "</td><td>" + source.getName() +
                    "</td><td>" + source.getUrl() +
                    "</td><td>" + source.getDescr() +
                    "</td><td>" + source.getParserClassName() +
                    "</td><td>" + source.getEnabled() +
                    "</td></tr>");
        }

        StringBuilder strGroups = new StringBuilder();
        for (GroupModel group : groups) {
            strGroups.append("<tr><td><a href=\"groups/" + group.getId() + "\">" + group.getId() + "</a>" +
                    "</td><td>" + group.getName() +
                    "</td><td>" + group.getDescr() +
                    "</td><td>" + Arrays.toString(group.getSources()) +
                    "</td><td>" + group.getDefaultGroup() +
                    "</td></tr>");
        }

        String newSources = "<form action=\"sources/new\" method=\"post\">" +
                "<input type=\"text\" placeholder=\"Name\" name=\"name\">" +
                "<input type=\"text\" placeholder=\"URL\" name=\"url\">" +
                "<input type=\"text\" placeholder=\"Description\" name=\"descr\">" +
                getParsersHTMLClassNames() +
                "<input type=\"submit\">" +
                "</form>";

        String newGroup = "<form action=\"groups/new\" method=\"post\">" +
                "<input type=\"text\" placeholder=\"Name\" name=\"name\">" +
                "<input type=\"text\" placeholder=\"Description\" name=\"descr\">" +
                "<input type=\"submit\">" +
                "</form>";

        return header +
                "<h3>Sources</h3><table style=\"width:100%\"><tr><th>ID</th><th>Name</th><th>URL</th><th>Description</th><th>Parser</th><th>Enabled</th></tr>" +
                strSources + "</table><br><hr><br>" +
                "<h3>Add new source</h3><strong>all fields are mandatory</strong>" + newSources +
                "<br><hr><br>" +
                "<h3>Groups</h3><table style=\"width:100%\"><tr><th>ID</th><th>Name</th><th>Description</th><th>Sources</th><th>Default</th></tr>" +
                strGroups +
                "</table><br><hr><br>" +
                "<h3>Add new group</h3><strong>all fields are mandatory</strong>" + newGroup +
                footer;
    }

    /*
    Returns known parsers' full class names in HTML string
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

    /*
    Show group edit page
     */
    @GET
    @Path("/groups/{groupid}")
    @Produces(MediaType.TEXT_HTML)
    public String EditGroup(@PathParam("groupid") Integer groupid) {
        StringBuilder sb = new StringBuilder(header);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class);
        q.setParameter("arg1",groupid);
        GroupModel group = (GroupModel)q.getSingleResult();
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

    /*
   Remove group from DB
    */
    @GET
    @Path("/groups/{groupid}/remove")
    @Produces(MediaType.TEXT_HTML)
    public String RemoveGroup(@PathParam("groupid") Integer groupid) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("delete GroupModel where id=:arg1");
        q.setParameter("arg1",groupid);
        q.executeUpdate();
        entityManager.getTransaction().commit();
        logger.info("Group " + groupid + " has been removed");
        return "<br>Return to " + ADMIN_PAGE_LINK;
    }

    /*
    Remove source from DB
     */
    @GET
    @Path("/sources/{sourceid}/remove")
    @Produces(MediaType.TEXT_HTML)
    public String RemoveSource(@PathParam("sourceid") Integer sourceid) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from RateModel where source=:arg1", RateModel.class);
        q.setParameter("arg1",sourceid);
        q.setMaxResults(1);
        List<Object> l = q.getResultList();
        String res;
        if (0 == l.size()) {
            q = entityManager.createQuery("delete SourceModel where id=:arg1");
            q.setParameter("arg1",sourceid);
            q.executeUpdate();
            res = "Success.";
        } else {
            res = "We have rates from this source in DB. Cannot be removed.";
        }
        entityManager.getTransaction().commit();
        logger.info("Source " + sourceid + " has been removed");
        return res + "<br>Return to " + ADMIN_PAGE_LINK;
    }

    /*
    Show source edit page
    */
    @GET
    @Path("/sources/{sourceid}")
    @Produces(MediaType.TEXT_HTML)
    public String EditSource(@PathParam("sourceid") Integer sourceid) {
        StringBuilder sb = new StringBuilder(header);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from SourceModel where id=:arg1", SourceModel.class);
        q.setParameter("arg1",sourceid);
        SourceModel source = (SourceModel)q.getSingleResult();
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
        sb.append("<script>var el = document.getElementById(\'parserclass\');\nel.value=\"" +
                source.getParserClassName() + "\"</script>");
        return sb.toString();
    }

    /*
    Persist group after edit
     */
    @POST
    @Path("/groups/{groupid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveGroup(@PathParam("groupid") Integer groupid,
                            @FormParam("source") List<Integer> sources,
                            @FormParam("name") String name,
                            @FormParam("descr") String descr,
                            @FormParam("default") Boolean dflt) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from GroupModel where id=:arg1", GroupModel.class);
        q.setParameter("arg1", groupid);
        GroupModel group = (GroupModel)q.getSingleResult();
        group.setSources(sources.toArray(new Integer[sources.size()]));
        group.setDescr(descr);
        group.setName(name);
        if (null != dflt) entityManager.createQuery("UPDATE GroupModel SET dflt=:state WHERE id<>:groupid").
                setParameter("state",false).setParameter("groupid",groupid).
                executeUpdate();
        group.setDefaultGroup((null == dflt ? false : true));
        entityManager.merge(group);
        entityManager.getTransaction().commit();
        logger.info("Group " + groupid + "has been edited");
        return "Success. Return to " + ADMIN_PAGE_LINK;
    }

    /*
    Persist source after edit
     */
    @POST
    @Path("/sources/{sourceid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveSource(@PathParam("sourceid") Integer groupid,
                            @FormParam("url") String url,
                            @FormParam("name") String name,
                            @FormParam("descr") String descr,
                            @FormParam("parserclass") String parser,
                            @FormParam("enabled") Boolean enabled) {
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery("from SourceModel where id=:arg1", SourceModel.class);
        q.setParameter("arg1", groupid);
        SourceModel source = (SourceModel)q.getSingleResult();
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

    /*
    Create new source
     */
    @POST
    @Path("/sources/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String CreateSource(@FormParam("name") String name, @FormParam("url") String url, @FormParam("descr") String descr, @FormParam("parserclass") String parserclass) {
        if ((null == name || 0 == name.length())
            || (null == url || 0 == url.length())
            || (null == descr || 0 == descr.length())
            || (null == parserclass || 0 == parserclass.length()))
            return "All form fields are mandatory. Return to " + ADMIN_PAGE_LINK;
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

    /*
    Create new group
     */
    @POST
    @Path("/groups/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String CreateGroup(@FormParam("name") String name, @FormParam("descr") String descr) {
        if ((null == name || 0 == name.length()) || (null == descr || 0 == descr.length()))
            return "All form fields are mandatory. Return to " + ADMIN_PAGE_LINK;
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

    /*
    Get application state
    */
    @GET
    @Path("/rest/state")
    @Produces(MediaType.APPLICATION_JSON)
    public String getState() {
        StringBuilder sb = new StringBuilder("{state: {sources: {");
        Map<Integer,Boolean> state = AppState.getState();
        int i = 0;
        for (Map.Entry<Integer,Boolean> entry : state.entrySet()) {
            if (i > 0) sb.append(","); else i++;
            sb.append("\"" + entry.getKey() + "\":" + (entry.getValue() ? "\"OK\"" : "\"ERROR\""));
        }
        sb.append("}}}");
        return sb.toString();
    }

    /*
    Get state for specific source
    */
    @GET
    @Path("/rest/state/{sourceid}")
    @Produces(MediaType.TEXT_HTML)
    public String getStateID(@PathParam("sourceid") Integer sourceid) {
        StringBuilder sb = new StringBuilder("<html><body>");
        Map<Integer,Boolean> state = AppState.getState();
        Boolean sourceState = state.get(sourceid);
        if (null == sourceState) sb.append("NO STATE");
        else sb.append(sourceState ? "OK" : "ERROR");
        sb.append("</body></html>");
        return sb.toString();
    }
}
