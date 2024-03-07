// Script per la gestione dell'apertura e chiusura delle showdoc-section
$(window).load(function() {
  jsf.ajax.addOnEvent(function(data) {
    if (data.status == 'success') {
      initCollapseButtons();
    }
  });
});

$(document).ready(function() {
  initCollapseButtons();
});

function initCollapseButtons() {

  var toggleButtons = $(".btn-collapse.showdoc-section-btn-collapse");

  if (toggleButtons.length === 0) {
    var open = $("#showdoc-section-script").attr("open-default") === 'true';

    var sections = $(".showdoc-section");
    sections.each(function (index) {
      createCollapseButton(index, $(this), open);
    });
  }
}

function createCollapseButton(index, $element, open) {

  var glyphicon = open ? "glyphicon-chevron-up" : "glyphicon-chevron-down";
  var collapse = open ? "collapse in" : "collapse";

  var id_container = "data_collapse_" + index;
  var id_button_icon = "button_icon_" + index;
  var id_icon = "section_icon_" + index;
  var $title = $element.find(".title");
  var $container = $title.next();
  var $icon = $title.find("div.pull-right").children(":first");

  var $button = $('<button type="button" data-toggle="collapse" data-target="#' + id_container + '" ' +
    'onclick="toggleCollapse(\''+id_button_icon+'\', \''+id_icon+'\')"' +
    'class="btn btn-xs btn-primary pull-right margin-left-10" title="#{i18n[\'dw4.collapse\']}">' +
    '<span id="'+id_button_icon+'" class="glyphicon '+ glyphicon +' btn-collapse showdoc-section-btn-collapse"></span></button>');
  $title.prepend($button);

  $container.addClass(collapse);
  $container.attr("id", id_container);
  $icon.attr("id", id_icon);
  if (!open)
    $icon.addClass("hidden");

}

function toggleCollapse(id_button, id_icon) {

  var $button = $("#"+id_button);

  $button.prop("disabled", true);
  $button.toggleClass("glyphicon-chevron-up glyphicon-chevron-down");

  var $icon = $("#"+id_icon);
  $icon.toggleClass("hidden");
  $button.prop("disabled", false);

}