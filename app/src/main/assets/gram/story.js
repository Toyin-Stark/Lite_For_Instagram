'use strict';

var options = {
    fireOnAttributesModification: true, // Defaults to false. Setting it to true would make arrive event fire on existing elements which start to satisfy selector after some modification in DOM attributes (an arrive event won't fire twice for a single element even if the option is true). If false, it'd only fire for newly created elements.
    onceOnly: false,                      // Defaults to false. Setting it to true would ensure that registered callbacks fire only once. No need to unbind the event if the attribute is set to true, it'll automatically unbind after firing once.
    existing: true                      // Defaults to false. Setting it to true would ensure that the registered callback is fired for the elements that already exist in the DOM and match the selector. If options.onceOnly is set, the callback is only called once with the first element matching the selector.
};
$(document).arrive("._2jziq",options, function(newPhoto) {

  $(newPhoto).click(function(e){
    e.stopPropagation();
   var stories = $(this).find("._ocij3").text();
    window.JSInterface.reloader(stories);

 })



});



function storyScan() {

    $("._2jziq").each(function(){

                                  var sbutton = document.createElement("button");
                                      sbutton.setAttribute("type","button");
                                      sbutton.setAttribute("id","story");
                                      sbutton.setAttribute("class","storyBtn");





                  if($(this).find("#story").length != 0){

                  }else{

                         var stories = $(this).find("._ocij3").text();
                           sbutton.addEventListener('click', function(e) {
                              e.preventDefault();
                              window.JSInterface.startVideo(stories,"story");
                           });

                         $(this).append(sbutton);


                  }




    });


}
