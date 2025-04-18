// Recuperer les tribunals

function loadTribunalInfos(event) {
	
    event.preventDefault();
	
    var typeTribunal = $("#type_tribunal").val();
    var idTribunal = $("#cour_appel").val();

    if (typeTribunal == "PREMIERE_INSTANCE") {
        loadPremieres(idTribunal);
    } else if (typeTribunal == "COUR_APPEL") {
        var formData = new FormData();
        formData.append("typeTribunal", $("#type_tribunal").val());
        formData.append("idTribunal", $("#cour_appel").val());
        loadJuges(formData);
    }
}

function loadJugesAjax() {
    var formData = new FormData();
    formData.append("typeTribunal", $("#type_tribunal").val());
    formData.append("idTribunal", $("#tribunal_premiere_instance").val());
    loadJuges(formData);
}

function loadPremieres(val) {
    $.ajax({
        type: "GET",
        url: "Client/ajouterDossier/CourAppel/" + val,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var premier = data["premiereInstances"];
            var html = "<option disabled selected>اختر محكمة ابتدائية</option>";
            for (const key in premier) {
                html = html + "<option value=\"" + premier[key]["id"] + "\">" + premier[key]["nom"] + "</option>";
            }
            $("#tribunal_premiere_instance").empty();
            $("#tribunal_premiere_instance").append(html);
        },
        error: function () {

        }
    });

}

function loadJuges(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/listeJuges",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            var juges = data["juges"];
            var html = "<option disabled selected>اختر القاضي</option>";
            for (const key in juges) {
                html = html + '<option value=' + juges[key]["matriculeJuge"] + '> ' + juges[key]["prenom"] + ' ' + juges[key]["nom"] + ' </option >';
            }
            $("#liste_juges").empty();
            $("#liste_juges").append(html);

        },
        error: function () {

        }
    })
}



// Ajout d'un dossier

function addFolderEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    var typeTribunal = $("#type_tribunal").val();
    if (typeTribunal == "PREMIERE_INSTANCE") {
        formData.append("tribunal", $("#tribunal_premiere_instance option:selected").text());
    } else if (typeTribunal == "COUR_APPEL") {
        formData.append("tribunal", $("#cour_appel option:selected").text());
    }

    if ($("#numero_dossier_2").val() === "") {
        formData.append("numero_dossier", "");
    } else {
    
    	var date = new Date($("#date_dossier").val());
    	var AnneeActuel = date.getFullYear();
    	
    	var CodeDossier = 1201;
    
    	var numeroDossier = $("#numero_dossier_2").val().padStart(4, '0');    	
    
    	var fullNumber = AnneeActuel + '/' + CodeDossier + '/' + numeroDossier;
    	
        formData.append("numero_dossier", fullNumber);
    }
    formData.append("date_dossier", $("#date_dossier").val());
    formData.append("date_accident", $("#date_accident").val());
    formData.append("date_session", $("#date_session").val());

    formData.append("juge", $("#liste_juges").val());
    formData.append("numero_national", $("#numero_national").val());

	formData.append("logged_in_user", $("#logged_in_user").val());

    fire_ajax_dossier(formData, fullNumber);

}

function fire_ajax_dossier(formData, fullNumber) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/dossier",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#dossierModalBody").empty();
                $("#dossierModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#dossierModalBtn").empty();
                $("#dossierModalBtn").append(btn);

                var numDoss = "رقم الملف : " + fullNumber;
                $("#numero_dossier").empty();
                $("#numero_dossier").append(numDoss);
                $("#numero_dossier").val(fullNumber);

            } else {
                var error = data["message"];
                $("#dossierModalBody").empty();
                $("#dossierModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#dossierModalBtn").empty();
                $("#dossierModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}



// ajout d'un victime

function addVictimeEvent(event) {

    event.preventDefault();
    var formData = new FormData();

    if ($("#check_victime").prop('checked') == true) {
        formData.append("cni_victime", $("#cni_victime").val());
        formData.append("prenom_victime", $("#prenom_victime").val());
        formData.append("nom_victime", $("#nom_victime").val());
        formData.append("etat_social_victime", $("#etat_social_victime").val());
        formData.append("nombre_enfants_victime", $("#nombre_enfants_victime").val());
        formData.append("salaire_annuel_victime", $("#salaire_annuel_victime").val());
        formData.append("date_naissance_victime", $("#date_naissance_victime").val());
        formData.append("job_victime", $("#job_victime").val());
        formData.append("lieu_naissance_victime", $("#lieu_naissance_victime").val());
        formData.append("sexe_victime", $(".sexe_victime:checked").val());
        formData.append("pere_victime", $("#pere_victime").val());
        formData.append("mere_victime", $("#mere_victime").val());
        formData.append("addresse_victime", $("#addresse_victime").val());
        formData.append("numero_dossier", $("#numero_dossier").val());

        fire_ajax_victime(formData);
    } else {
        formData.append("id_victime", $('#liste_victimes').val());
        formData.append("numero_dossier", $("#numero_dossier").val());
        insert_victime(formData);
    }



}

function fire_ajax_victime(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/victime",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#victimeModalBody").empty();
                $("#victimeModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#victimeModalBtn").empty();
                $("#victimeModalBtn").append(btn);

                var etatVict = "2 - وضعية الضحية [" + $('#prenom_victime').val() + " " + $('#nom_victime').val() + "]";
                $("#etatVictime").empty();
                $("#etatVictime").append(etatVict);
                $("#etatVictime").val(data[1]['id']);

                var avocatVict = "3 - محامي الضحية [" + $('#prenom_victime').val() + " " + $('#nom_victime').val() + "]";
                $("#avocatVictime").empty();
                $("#avocatVictime").append(avocatVict);
                $("#avocatVictime").val(data[1]['id']);

                var victime_element = '<tr><td>' + data[1]["cin"] + '</td><td>' + data[1]["prenom"] + '</td > <td>' + data[1]["nom"] + '</td> <td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_victime_body").append(victime_element);

            } else if (data[0]["error"] = "error") {
                var error = data[0]["message"];
                $("#victimeModalBody").empty();
                $("#victimeModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#victimeModalBtn").empty();
                $("#victimeModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}

function insert_victime(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertVictime",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#victimeModalBody").empty();
                $("#victimeModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#victimeModalBtn").empty();
                $("#victimeModalBtn").append(btn);

                var etatVict = "2 - وضعية الضحية [" + data[1]["prenom"] + " " + data[1]["nom"] + "]";
                $("#etatVictime").empty();
                $("#etatVictime").append(etatVict);
                $("#etatVictime").val(data[1]['id']);

                var avocatVict = "3 - محامي الضحية [" + data[1]["prenom"] + " " + data[1]["nom"] + "]";
                $("#avocatVictime").empty();
                $("#avocatVictime").append(avocatVict);
                $("#avocatVictime").val(data[1]['id']);

                var victime_element = '<tr><td>' + data[1]["cin"] + '</td><td>' + data[1]["prenom"] + '</td > <td>' + data[1]["nom"] + '</td> <td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_victime_body").append(victime_element);

            } else if (data[0]["error"] = "error") {
                var error = data[0]["message"];
                $("#victimeModalBody").empty();
                $("#victimeModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#victimeModalBtn").empty();
                $("#victimeModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}

function updateEtatVictime() {
    var formData = new FormData();
    formData.append("id_victime", $('#etatVictime').val());
    formData.append("etat_victime", $(".etat_victime:checked").val());

    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/updateVictime",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
            } else if (data[0]["error"] == "error") {
            }
        },
        error: function () {

        }
    })
}


// Ajout d'un dawi

function addDawiEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_dawi").prop('checked') == true) {
        formData.append("relation_avec_victime", $('#relation_dawi').val());
        formData.append("droit_compensation", $('.droit_compensation:checked').val());
        formData.append("id_victime", $("#etatVictime").val());

        formData.append("cni", $('#cni_famille_victime').val());
        formData.append("nom", $('#nomfam_famille_victime').val());
        formData.append("prenom", $('#prenom_famille_victime').val());
        formData.append("date_naissance", $('#naissance_famille_victime').val());
        formData.append("etat_sociale", $('#etat_sociale_famille_victime').val());
        formData.append("job", $('#job_famille_victime').val());
        formData.append("addresse", $('#addresse_famille_victime').val());

        fire_ajax_dawi(formData);
    } else {

        formData.append("id_victime", $("#etatVictime").val());
        formData.append("id_dawi", $('#liste_dawis').val());

        formData.append("relation_avec_victime", $('#relation_dawi').val());
        formData.append("droit_compensation", $('.droit_compensation:checked').val());

        insert_dawi(formData);
    }
}

function fire_ajax_dawi(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/dawi",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $('#dawiModalBody').empty();
                $('#dawiModalBody').append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $('#dawiModalBtn').empty();
                $('#dawiModalBtn').append(btn);

            } else {
                var error = data["message"];
                $('#dawiModalBody').empty();
                $('#dawiModalBody').append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $('#dawiModalBtn').empty();
                $('#dawiModalBtn').append(btn);

            }

        },
        error: function () {

        }
    })
}


function insert_dawi(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertDawi",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $('#dawiModalBody').empty();
                $('#dawiModalBody').append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $('#dawiModalBtn').empty();
                $('#dawiModalBtn').append(btn);

            } else {
                var error = data["message"];
                $('#dawiModalBody').empty();
                $('#dawiModalBody').append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $('#dawiModalBtn').empty();
                $('#dawiModalBtn').append(btn);

            }

        },
        error: function () {

        }
    })
}


// Ajout d'un avocat pour victime

function addAvocatVictimeEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_avocat_victime").prop('checked') == true) {
        formData.append("cni_avocat_victime", $("#cni_avocat_victime").val());
        formData.append("prenom_avocat_victime", $("#prenom_avocat_victime").val());
        formData.append("nom_avocat_victime", $("#nom_avocat_victime").val());
        formData.append("barreau_victime", $("#barreau_victime").val());
        formData.append("addresse_avocat_victime", $("#addresse_avocat_victime").val());

        formData.append("id_victime", $("#etatVictime").val());

        fire_ajax_avocat_victime(formData);

    } else {
        formData.append("cni_avocat_victime", $('#liste_avocats_victime').val());
        formData.append("id_victime", $("#etatVictime").val());

        insert_avocat_victime(formData);
    }

}

function fire_ajax_avocat_victime(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/avocatVictime",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#avocatVictimeModalBody").empty();
                $("#avocatVictimeModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#avocatVictimeModalBtn").empty();
                $("#avocatVictimeModalBtn").append(btn);

            } else {
                var error = data["message"];
                $("#avocatVictimeModalBody").empty();
                $("#avocatVictimeModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#avocatVictimeModalBtn").empty();
                $("#avocatVictimeModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


function insert_avocat_victime(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertAvocatVictime",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#avocatVictimeModalBody").empty();
                $("#avocatVictimeModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#avocatVictimeModalBtn").empty();
                $("#avocatVictimeModalBtn").append(btn);

            } else {
                var error = data["message"];
                $("#avocatVictimeModalBody").empty();
                $("#avocatVictimeModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#avocatVictimeModalBtn").empty();
                $("#avocatVictimeModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


// ajout d'un accuse

function addAccuseEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_accuse").prop('checked') == true) {
        formData.append("cni_accuse", $("#cni_accuse").val());
        formData.append("prenom_accuse", $("#prenom_accuse").val());
        formData.append("nom_accuse", $("#nom_accuse").val());
        formData.append("etat_social_accuse", $("#etat_social_accuse").val());
        formData.append("nombre_enfants_accuse", $("#nombre_enfants_accuse").val());
        formData.append("assurance_accuse", $("#assurance_accuse").val());
        formData.append("date_naissance_accuse", $("#date_naissance_accuse").val());
        formData.append("job_accuse", $("#job_accuse").val());
        formData.append("lieu_naissance_accuse", $("#lieu_naissance_accuse").val());
        formData.append("sexe_accuse", $(".sexe_accuse:checked").val());
        formData.append("pere_accuse", $("#pere_accuse").val());
        formData.append("mere_accuse", $("#mere_accuse").val());
        formData.append("addresse_accuse", $("#addresse_accuse").val());

        formData.append("numero_dossier", $("#numero_dossier").val());
        formData.append("charge_accuse", $("#charge_accuse").val());

        fire_ajax_accuse(formData);
    } else {

        formData.append("id_accuse", $('#liste_accuses').val());
        formData.append("numero_dossier", $("#numero_dossier").val());
        formData.append("charge_accuse", $("#charge_accuse").val());

        insert_accuse(formData);
    }
}

function fire_ajax_accuse(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/accuse",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#accuseModalBody").empty();
                $("#accuseModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#accuseModalBtn").empty();
                $("#accuseModalBtn").append(btn);

                var offCivil = "2 - المسؤول المدني [" + $('#prenom_accuse').val() + " " + $('#nom_accuse').val() + "]";
                $("#officierCivil").empty();
                $("#officierCivil").append(offCivil);
                $("#officierCivil").val(data[1]['id']);

                var avocatAcc = "3 - محامي المتهم [" + $('#prenom_accuse').val() + " " + $('#nom_accuse').val() + "]";
                $("#avocatAccuse").empty();
                $("#avocatAccuse").append(avocatAcc);
                $("#avocatAccuse").val(data[1]['id']);

                var accuse_element = '<tr><td>' + data[1]["cin"] + '</td><td>' + data[1]["prenom"] + '</td > <td>' + data[1]["nom"] + '</td> <td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_accuse_body").append(accuse_element);

            } else if (data[0]["error"] == "error") {
                var error = data[0]["message"];
                $("#accuseModalBody").empty();
                $("#accuseModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#accuseModalBtn").empty();
                $("#accuseModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}

function insert_accuse(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertAccuse",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#accuseModalBody").empty();
                $("#accuseModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#accuseModalBtn").empty();
                $("#accuseModalBtn").append(btn);

                var offCivil = "2 - المسؤول المدني [" + data[1]["prenom"] + " " + data[1]["nom"] + "]";
                $("#officierCivil").empty();
                $("#officierCivil").append(offCivil);
                $("#officierCivil").val(data[1]['id']);

                var avocatAcc = "3 - محامي المتهم [" + data[1]["prenom"] + " " + data[1]["nom"] + "]";
                $("#avocatAccuse").empty();
                $("#avocatAccuse").append(avocatAcc);
                $("#avocatAccuse").val(data[1]['id']);

                var accuse_element = '<tr><td>' + data[1]["cin"] + '</td><td>' + data[1]["prenom"] + '</td > <td>' + data[1]["nom"] + '</td> <td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_accuse_body").append(accuse_element);

            } else if (data[0]["error"] == "error") {
                var error = data[0]["message"];
                $("#accuseModalBody").empty();
                $("#accuseModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#accuseModalBtn").empty();
                $("#accuseModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


// Ajout d'un officier civil pour victime

function addOfficierCivilEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_officier_civil").prop('checked') == true) {
        formData.append("cni_officier_civil", $("#cni_officier_civil").val());
        formData.append("prenom_officier_civil", $("#prenom_officier_civil").val());
        formData.append("nom_officier_civil", $("#nom_officier_civil").val());
        formData.append("assurance_officier_civil", $("#assurance_officier_civil").val());
        formData.append("addresse_officier_civil", $("#addresse_officier_civil").val());

        formData.append("id_accuse", $("#officierCivil").val());

        fire_ajax_officier_civil(formData);
    } else {
        formData.append("cni_officier_civil", $("#liste_officier_civils").val());
        formData.append("id_accuse", $("#officierCivil").val());

        insert_officier_civil(formData);
    }



}

function fire_ajax_officier_civil(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/officierCivil",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#officierCivilModalBody").empty();
                $("#officierCivilModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#officierCivilModalBtn").empty();
                $("#officierCivilModalBtn").append(btn);

            } else {

                var error = data["message"];
                $("#officierCivilModalBody").empty();
                $("#officierCivilModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#officierCivilModalBtn").empty();
                $("#officierCivilModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


function insert_officier_civil(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertOfficierCivil",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#officierCivilModalBody").empty();
                $("#officierCivilModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#officierCivilModalBtn").empty();
                $("#officierCivilModalBtn").append(btn);

            } else {

                var error = data["message"];
                $("#officierCivilModalBody").empty();
                $("#officierCivilModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#officierCivilModalBtn").empty();
                $("#officierCivilModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}



// Ajout d'un avocat pour accuse

function addAvocatAccuseEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_avocat_accuse").prop('checked') == true) {
        formData.append("cni_avocat_accuse", $("#cni_avocat_accuse").val());
        formData.append("prenom_avocat_accuse", $("#prenom_avocat_accuse").val());
        formData.append("nom_avocat_accuse", $("#nom_avocat_accuse").val());
        formData.append("barreau_accuse", $("#barreau_accuse").val());
        formData.append("addresse_avocat_accuse", $("#addresse_avocat_accuse").val());

        formData.append("id_accuse", $("#avocatAccuse").val());

        fire_ajax_avocat_accuse(formData);
    } else {
        formData.append("cni_avocat_accuse", $('#liste_avocats_accuse').val());
        formData.append("id_accuse", $("#officierCivil").val());

        insert_avocat_accuse(formData);
    }



}

function fire_ajax_avocat_accuse(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/avocatAccuse",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data["error"] == "success") {

                var success = data["message"];
                $("#avocatAccuseModalBody").empty();
                $("#avocatAccuseModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#avocatAccuseModalBtn").empty();
                $("#avocatAccuseModalBtn").append(btn);

            } else {

                var error = data["message"];
                $("#avocatAccuseModalBody").empty();
                $("#avocatAccuseModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#avocatAccuseModalBtn").empty();
                $("#avocatAccuseModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


function insert_avocat_accuse(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertAvocatAccuse",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {

            if (data["error"] == "success") {

                var success = data["message"];
                $("#avocatAccuseModalBody").empty();
                $("#avocatAccuseModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#avocatAccuseModalBtn").empty();
                $("#avocatAccuseModalBtn").append(btn);

            } else {

                var error = data["message"];
                $("#avocatAccuseModalBody").empty();
                $("#avocatAccuseModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#avocatAccuseModalBtn").empty();
                $("#avocatAccuseModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}



// ajout d'un temoin

function addTemoinEvent(event) {
    event.preventDefault();
    var formData = new FormData();

    if ($("#check_temoin").prop('checked') == true) {
        formData.append("cni_temoin", $("#cni_temoin").val());
        formData.append("prenom_temoin", $("#prenom_temoin").val());
        formData.append("nom_temoin", $("#nom_temoin").val());
        formData.append("etat_sociale_temoin", $("#etat_sociale_temoin").val());
        formData.append("date_naissance_temoin", $("#date_naissance_temoin").val());
        formData.append("job_temoin", $("#job_temoin").val());
        formData.append("lieu_naissance_temoin", $("#lieu_naissance_temoin").val());
        formData.append("sexe_temoin", $(".sexe_temoin:checked").val());
        formData.append("pere_temoin", $("#pere_temoin").val());
        formData.append("mere_temoin", $("#mere_temoin").val());
        formData.append("addresse_temoin", $("#addresse_temoin").val());

        formData.append("numero_dossier", $("#numero_dossier").val());

        fire_ajax_temoin(formData);
    } else {

        formData.append("cni_temoin", $('#liste_temoins').val());
        formData.append("numero_dossier", $("#numero_dossier").val());

        insert_temoin(formData);
    }

}


function fire_ajax_temoin(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/temoin",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#temoinModalBody").empty();
                $("#temoinModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#temoinModalBtn").empty();
                $("#temoinModalBtn").append(btn);

                var temoin_element = '<tr><td>' + data[1]["cinTemoin"] + '</td><td>' + data[1]["prenom"] + '</td><td>' + data[1]["nom"] + '</td ><td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_temoin_body").append(temoin_element);

            } else if (data[0]["error"] == "error") {
                var error = data[0]["message"];
                $("#temoinModalBody").empty();
                $("#temoinModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#temoinModalBtn").empty();
                $("#temoinModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}


function insert_temoin(formData) {
    $.ajax({
        type: "POST",
        url: "Client/ajouterDossier/insertTemoin",
        processData: false,
        contentType: false,
        cache: false,
        data: formData,
        timeout: 600000,
        success: function (data) {
            if (data[0]["error"] == "success") {
                var success = data[0]["message"];
                $("#temoinModalBody").empty();
                $("#temoinModalBody").append(success);

                var btn = "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">حفظ</button>";
                $("#temoinModalBtn").empty();
                $("#temoinModalBtn").append(btn);

                var temoin_element = '<tr><td>' + data[1]["cinTemoin"] + '</td><td>' + data[1]["prenom"] + '</td><td>' + data[1]["nom"] + '</td ><td>' + data[1]["proffession"] + '</td><td class="text-right"><div class="btn-group"><button class="btn-white btn btn-xs">إطلاع</button><button class="btn-white btn btn-xs">حذف</button></div></td></tr>';
                $("#table_temoin_body").append(temoin_element);

            } else if (data[0]["error"] == "error") {
                var error = data[0]["message"];
                $("#temoinModalBody").empty();
                $("#temoinModalBody").append(error);

                var btn = "<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">إعادة المحاولة</button>";
                $("#temoinModalBtn").empty();
                $("#temoinModalBtn").append(btn);

            }

        },
        error: function () {

        }
    })
}