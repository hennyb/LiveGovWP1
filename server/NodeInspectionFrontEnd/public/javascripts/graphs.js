// This file handels the graph display
(function() {
  window.apiUrl = window.apiUrl || "http://localhost:3000/api/1";

  var fplot = function(e,data,options){
    var jqParent, jqHidden;
    if (e.offsetWidth <=0 || e.offetHeight <=0){
      // lets attempt to compensate for an ancestor with display:none
      jqParent = $(e).parent();
      jqHidden = $("<div style='visiblity:hidden'></div>");
      $('body').append(jqHidden);
      jqHidden.append(e);
    }

    var plot=$.plot(e,data,options);

    // if we moved it above, lets put it back
    if (jqParent){
       jqParent.append(e);
       jqHidden.remove();
    }

    return plot;
  };

  function showDataInId (id, data) {
    if(!data) { console.log("No data for", id); return; }
    var labelId = id.replace("Plot", "Label");
    $(labelId).text(data.length);
    var avgX = [];
    var maxX = [];
    var minX = [];

    var avgY = [];
    var maxY = [];
    var minY = [];

    var avgZ = [];
    var maxZ = [];
    var minZ = [];

    data.forEach(function (item) {
      var ts = new Date(item.midTime);
      avgX.push([ts, item.avgX]);
      maxX.push([ts, item.maxX]);
      minX.push([ts, item.minX]);

      avgY.push([ts, item.avgY]);
      maxY.push([ts, item.maxY]);
      minY.push([ts, item.minY]);

      avgZ.push([ts, item.avgZ]);
      maxZ.push([ts, item.maxZ]);
      minZ.push([ts, item.minZ]);
    });
    
    var plotData = [
      { label: "avgX", data: avgX },
      { label: "maxX", data: maxX },
      { label: "minX", data: minX },
      { label: "avgY", data: avgY },
      { label: "maxY", data: maxY },
      { label: "minY", data: minY },
      { label: "avgZ", data: avgZ },
      { label: "maxZ", data: maxZ },
      { label: "minZ", data: minZ }
    ];
    var plotOptions = {
      series: {
        lines: { show: true },
        points: { show: true }
      },
      grid: {
        hoverable: true,
        clickable: true
      },
      xaxis: { 
        mode: "time"
      },
      selection: {
        mode: "x"
      }
    };
    fplot($(id)[0], plotData, plotOptions);
  }

  function zoom(id, ranges) {
    $.ajax({
      url: apiUrl + "/" + id + "/acc?startTime=" + ranges.xaxis.from.toFixed(1) + "&endTime=" +ranges.xaxis.to.toFixed(1)
    }).done(function (data) {
      showDataInId("#accPlot", data);
    });
    $.ajax({
      url: apiUrl + "/" + id + "/lac?startTime=" + ranges.xaxis.from.toFixed(1) + "&endTime=" +ranges.xaxis.to.toFixed(1)
    }).done(function (data) {
      showDataInId("#lacPlot", data);
    });
    $.ajax({
      url: apiUrl + "/" + id + "/gra?startTime=" + ranges.xaxis.from.toFixed(1) + "&endTime=" +ranges.xaxis.to.toFixed(1)
    }).done(function (data) {
      showDataInId("#graPlot", data);
    });
    var meta = "From: " + Math.floor(ranges.xaxis.from) + " To: " + Math.floor(ranges.xaxis.to);
    $(".metaLabel").text(meta);
  }

  function showAccelerometerForId(id) {
    $.ajax({
      url: apiUrl + "/" + id + "/acc"
    }).done(function (data) {
      showDataInId("#accPlot", data);
    });
    $("#accPlot").bind('plotselected', function (event, ranges) {
      zoom(id, ranges);
    });
  }

  function showLinearAccelerationForId(id) {
    $.ajax({
      url: apiUrl + "/" + id + "/lac"
    }).done(function (data) {
      showDataInId("#lacPlot", data);
    });
    $("#lacPlot").bind('plotselected', function (event, ranges) {
      zoom(id, ranges);
    });
  }

  function showGravityForId(id) {
    $.ajax({
      url: apiUrl + "/" + id + "/gra"
    }).done(function (data) {
      showDataInId("#graPlot", data);
    });
    $("#graPlot").bind('plotselected', function (event, ranges) {
      zoom(id, ranges);
    });
  }
  

  window.showAccelerometerForId = showAccelerometerForId;
  window.showLinearAccelerationForId = showLinearAccelerationForId;
  window.showGravityForId = showGravityForId;
})();