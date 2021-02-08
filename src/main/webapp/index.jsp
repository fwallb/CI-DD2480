<%@page import="java.util.ArrayList"%>
<html>
    <%
        // ArrayList<Actor> list = new ArrayList<Actor>();
        // list = (ArrayList<Actor>) request.getAttribute("actors");
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(5);
        list.add(9);
    %>
    <head>
        <title>Java Code Geeks Snippets - Sample JSP Page</title>
    </head>
    <body>
        Current date is: <%=new java.util.Date()%>

        <table>
            <thead>
                <tr>
                    <th>Column 1</th>
                    <th>Column 2</th>
                    <th>Column 3</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    for(int i : list) {
                %>
                    <tr>
                        <td><%=i%></td>
                        <td><%=i + 1%></td>
                        <td><%=i + 2%></td>
                    </tr>
                <%
                };
                %>
            </tbody>
        </table>
    </body>
</html>