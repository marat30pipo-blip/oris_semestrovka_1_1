$(document).ready(function () {
    $("#matchStatus").on("change", function () {
        let select = $(this);

        $("#matchesMessage").text("");
        select.prop("disabled", true);

        $.ajax({
            url: "/matches/filter",
            type: "GET",
            dataType: "html",
            data: {
                tournamentId: select.attr("data-tournament-id"),
                status: select.val()
            },
            success: function (html) {
                $("#matchesContainer").html(html);
            },
            error: function () {
                $("#matchesMessage").text(
                    "Не удалось загрузить матчи. Попробуйте ещё раз."
                );
            },
            complete: function () {
                select.prop("disabled", false);
            }
        });
    });
});