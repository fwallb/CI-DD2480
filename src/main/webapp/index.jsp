<%@page import="java.util.ArrayList"%>
<%@page import="ci.Database"%>

<html>
    <%
        Database db = new Database();
        ArrayList<String[]> list = db.getHistory();
    %>
    <head>
        <title>CI-DD2480</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Permanent+Marker">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Ubuntu+Mono">
        <style>
            *, h1 {
                margin: 0;
                padding: 0;
            }
            header {
                font-family: "Permanent Marker", sans-serif;
                background: #272727;
                color: #F5F5F5;
                padding: 25px;
            }
            .flex {
                display: flex;
                align-items: center;
                font-family: monospace;
            }
            button {
                margin-left: auto;
            }
            .log {
                background: #272727;
                color: #F5F5F5;
                font-family: "Ubuntu Mono", sans-serif;
                padding: 10px;
            }
            .log > p {
                line-height: 140%;
                margin: 0;
            }
            .hide {
                display: none;
            }
        
        </style>
    </head>
    <body>
        <header>
            <h1>CI-DD2480</h1>
            <h6>Continuous Integration made fun! XD</h6>
        </header>
        <main class="collection">
            <% 
                for(String[] build : list) {
            %>
                <div class="build collection-item">
                    <div class="flex" id="#<%=build[0]%>">
                        <p><%=build[1]%></p>
                        &nbsp
                        <p>Commit-ID: <span><%=build[0]%></span></p>
                        <button class="btn waves-effect waves-light" onClick="toggleBuildLog(this)">View build log</button>
                    </div>
                    <div class="log hide">
                        <p><%=build[2]%></p>
                    </div>
                </div>
            <%
            };
            %>
        </main>
        <script>
            let expectedHash;
            function openLog(log){
                log.classList.remove('hide')
            }
            function closeLog(log){
                log.classList.add('hide')
            }
            function toggleBuildLog(button) {
                if(button.innerHTML === 'View build log'){
                    openLog(button.parentNode.nextElementSibling)
                    let hash = button.previousElementSibling.lastChild.innerHTML;
                    expectedHash = hash;
                    window.location.hash = hash;
                    button.innerHTML = 'Hide build log';
                }
                else {
                    closeLog(button.parentNode.nextElementSibling)
                    button.innerHTML = 'View build log';
                    history.replaceState(null, null, ' ');
                    expectedHash = "";
                }
            }

            function hashChangeHandler() {
                if(window.location.hash === ('#' + expectedHash))
                    return;
                expectedHash = window.location.hash;
                
                let build = document.getElementById(window.location.hash);
                if(build){
                    let button = build.lastElementChild;
                    if(button.innerHTML === 'View build log'){
                        openLog(button.parentNode.nextElementSibling)
                        button.innerHTML = 'Hide build log';
                    }
                }       
            }
            window.addEventListener('hashchange', hashChangeHandler);
            hashChangeHandler();
        </script>
    </body>
</html>