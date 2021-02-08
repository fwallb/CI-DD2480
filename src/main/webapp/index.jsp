<%@page import="java.util.ArrayList"%>
<html>
    <%
        //Database db = new Database();
        //ArrayList<String[]> list = db.getHistory();
        /* Mock data */
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{"1001", "2020-12-24", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam libero nunc, ullamcorper a felis id, molestie bibendum sapien. Ut feugiat cursus mollis. Maecenas feugiat pellentesque magna vitae laoreet. Sed blandit, dolor non viverra bibendum, dolor velit viverra est, condimentum condimentum nunc nisi eget metus. Mauris rhoncus nunc est, ac dignissim ligula viverra at. Praesent sagittis iaculis justo, sed mattis sem pellentesque a. Pellentesque vel dui finibus, consequat nibh non, bibendum justo. Nullam blandit malesuada mi ut vestibulum. Phasellus nec magna eu risus ultricies lacinia. Donec iaculis feugiat lacus, ut vehicula urna rhoncus id."});
        list.add(new String[]{"1002", "2021-01-01", "Quisque eu ligula ex. Integer ligula ipsum, efficitur ut commodo non, bibendum eu neque. Nam ornare, dolor eu ultricies sagittis, lorem lacus dictum sem, id scelerisque metus ante ac massa. Quisque augue lorem, facilisis et fringilla ut, efficitur eu justo. Pellentesque suscipit ante quis enim congue semper. Cras et semper elit, non condimentum nibh. Aliquam consequat dolor id urna feugiat posuere. Nullam id nulla sem. Suspendisse potenti. Aliquam sed urna viverra, ultrices libero a, finibus eros. Nam lobortis elit hendrerit nunc viverra, vitae congue tortor elementum. Sed sit amet dolor id ante blandit gravida. Fusce tincidunt nisl mauris, non pulvinar libero dapibus quis. Aliquam vestibulum quis eros quis consectetur. Quisque in nisi nisl."});
        list.add(new String[]{"1003", "1998-03-03", "Curabitur rutrum quam bibendum, cursus nisl vitae, rutrum lacus. Nunc imperdiet mollis vulputate. Ut at ante in odio accumsan porttitor. Integer eget purus vel lorem efficitur aliquam id eget est. Donec ullamcorper a augue ac maximus. Maecenas ultrices eleifend elit et feugiat. Sed dignissim rutrum libero, in ultricies turpis aliquam vitae."});
        list.add(new String[]{"1004", "2007-05-14", "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse rhoncus efficitur enim, at ultrices lacus lacinia non. Suspendisse potenti. Nunc aliquet, mauris in dignissim vehicula, massa dolor tristique felis, eget dictum turpis nisl vel quam. Donec ac bibendum dolor. Donec a turpis elementum, aliquet massa sit amet, ullamcorper ipsum. Aenean vel odio a eros maximus dapibus. Praesent consequat ipsum in faucibus tempor. Mauris luctus risus a lacus rhoncus, in volutpat diam lobortis. Mauris tincidunt ex libero, in hendrerit libero rutrum sed. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum interdum mi erat. Donec rutrum urna eu porttitor lacinia."});
        list.add(new String[]{"1005", "2002-01-26", "Praesent fermentum lorem et auctor iaculis. Nulla nunc nisl, tempus at semper in, eleifend et sapien. Suspendisse pretium orci metus, sit amet tempus urna tempus vitae. Vestibulum lacinia ac elit quis viverra. Proin ac mauris non neque tristique imperdiet. Aliquam tincidunt mauris et arcu vehicula hendrerit. Fusce aliquam venenatis eros, sit amet consectetur justo mollis a. Cras nec luctus lorem. Sed sit amet lobortis sapien. Phasellus feugiat pretium erat, ac congue sem condimentum interdum. Curabitur sed volutpat ante. In vitae odio libero. Morbi ut mi a risus dictum finibus. Maecenas nec cursus ex. Quisque luctus non nisl ac maximus."});
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
                    <div class="flex">
                        <%=build[1]%>
                        Commit-ID: <%=build[0]%>
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
            function toggleBuildLog(button) {
                button.parentNode.nextSibling.nextSibling.classList.toggle('hide');
                button.innerHTML = (button.innerHTML === 'View build log')? 'Hide build log': 'View build log';
            }
        </script>
    </body>
</html>