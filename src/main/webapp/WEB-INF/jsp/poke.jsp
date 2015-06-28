<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: trevyn
  Date: 1/17/15
  Time: 1:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <jsp:include page="parts/meta.jsp"/>

    <title>Trevyn Langsford</title>

    <jsp:include page="parts/resources.jsp"/>
</head>

<body>

<jsp:include page="parts/header.jsp"/>

<div class="container">
    <h1 class="page-header">Personal Pokemon Sprites</h1>

    <div role="tabpanel">

        <!-- Nav tabs -->
        <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" class="active"><a href="#webapp" role="tab" data-toggle="tab">Web</a></li>
            <li role="presentation"><a href="#bulk" role="tab" data-toggle="tab">Bulk Upload</a></li>
        </ul>

        <!-- Tab panes -->
        <div class="tab-content">
            <br>
            <div role="tabpanel" class="tab-pane fade in active" id="webapp">
                <div class="row">
                    <div class="col-sm-4">
                        <form>
                            <div class="form-group">
                                <label class="control-label" for="inputText">Text:</label>
                                <input type="text" class="form-control" id="inputText">
                            </div>
                            <div class="form-group">
                                <label class="control-label" for="inputPokemon">Pokemon Name:</label>
                                <select class="form-control" id="inputPokemon"
                                        data-placeholder="Leave blank for random">
                                    <option value=""></option>
                                    <option value="">Random Pokemon</option>
                                    <c:forEach var="pokemon" items="${pokemonList}">
                                        <option value="${pokemon.nationalId}">${pokemon.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="row" id="regionSelection">
                                <div class="col-sm-6">
                                    <label class="radio-inline">
                                        <input type="radio" name="regionOptions" id="allRegionsRadio" value="allRegions"
                                               checked>
                                        All Regions
                                    </label>
                                </div>
                                <div class="col-sm-6">
                                    <label class="radio-inline">
                                        <input type="radio" name="regionOptions" id="pickRegionsRadio"
                                               value="pickRegions"> Choose Regions
                                    </label>

                                    <div class="form-group" id="regions">
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="kanto" disabled> Kanto
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="johto" disabled> Johto
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="hoenn" disabled> Hoenn
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="sinnoh" disabled> Sinnoh
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="unova" disabled> Unova
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" id="kalos" disabled> Kalos
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button type="button" id="submitPokeForm" class="btn btn-primary btn-block pull-right">
                                Submit
                            </button>
                            <br><br>

                            <%--<div class="alert alert-info" role="alert">Mega evolutions and alternate forms are not--%>
                                <%--currently--%>
                                <%--supported and will be reverted to their regular sprite. Some pokemon may result in--%>
                                <%--MissingNo.--%>
                            <%--</div>--%>
                        </form>
                    </div>
                    <div class="col-sm-8">
                        <img class="img-responsive center-block" style="padding: 1em;" id="outputImg" src=""/>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade" id="bulk">
                <div class="row">
                    <div class="col-sm-4">
                        <label for="instructionsWell">Instructions: </label>

                        <div id="instructionsWell" class="well">
                            <p>
                                Here is an example of what the server can parse. The first one provides both a name and
                                a pokemon, while the second one provides a name and regions. A random pokemon is selected
                                from the regions provided, which are comma-separated. If the same name is provided more
                                than one, a number will be prepended to the file name to differentiate the files.
                            </p>
                            <pre>
[
    {
        "text": "personName",
        "pokemon": "pikachu"
    },
    {
        "text": "personName",
        "regions": "kanto,hoenn"
    }
]</pre>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <form action="bulkupload" id="bulkUploadForm" method="get">
                            <div class="form-group">
                                <label class="control-label" for="bulkInput">Paste JSON here:</label>
                                <textarea id="bulkInput" name="bulkInput" class="form-control"
                                          rows="10" style="resize: none;"></textarea>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" id="useFile"> Upload File
                                </label>
                            </div>
                            <div class="form-group">
                                <input type="file" id="fileUpload" name="fileUpload" disabled>
                                <p class="help-block">Only valid JSON files are accepted</p>
                            </div>
                            <button type="submit" id="submitBulk" class="btn btn-primary btn-block pull-right">
                                Submit
                            </button>
                        </form>
                    </div>
                </div>

            </div>
        </div>

    </div>

</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/poke/poke.js"></script>
<jsp:include page="parts/footer.jsp"/>
</body>
</html>
