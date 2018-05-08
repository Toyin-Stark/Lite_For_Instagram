'use strict';
var options = {
    fireOnAttributesModification: true, // Defaults to false. Setting it to true would make arrive event fire on existing elements which start to satisfy selector after some modification in DOM attributes (an arrive event won't fire twice for a single element even if the option is true). If false, it'd only fire for newly created elements.
    onceOnly: false,                      // Defaults to false. Setting it to true would ensure that registered callbacks fire only once. No need to unbind the event if the attribute is set to true, it'll automatically unbind after firing once.
    existing: true                      // Defaults to false. Setting it to true would ensure that the registered callback is fired for the elements that already exist in the DOM and match the selector. If options.onceOnly is set, the callback is only called once with the first element matching the selector.
};
$(document).arrive("._4rbun",options, function(newPhoto) {

photoScan();


});



function photoScan() {

if( window.location.href.indexOf("www.instagram.com/p/") != -1 ||  window.location.href.indexOf("www.instagram.com/v/") != -1 ||  window.location.href.indexOf("www.instagram.com/") != -1){
    $("._4rbun").each(function(){

                                      var button = document.createElement("button");
                                      button.setAttribute("type","button");
                                      button.setAttribute("id","save");
                                      button.setAttribute("class","downloadBtn");
                                      button.innerHTML = 'Download';





                  if($(this).parent().closest("._sxolz").find("#save").length != 0){

                  }else{

                         var blinks = $(this).closest("article").find("._djdmk").attr("href");

                         $(button).data('src',blinks);
                         $(button).data('type',"photo");

                         if($(this).parent().closest("._sxolz").length != 0){
                          button.innerHTML = "Download Photo";

                           button.addEventListener('click', function(e) {
                              e.preventDefault();
                              window.JSInterface.startVideo(blinks,"photo");
                           });

                         $(this).parent().closest("._sxolz").append(button);

                         }




                  }




      });
  }else{

  }

}
