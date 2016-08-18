
function setProgress(id,value){
	$("#"+id).css("width",value);
	$("#"+id).html(value);
}

/**
	 * 请求任务进度
	 */
function queryTaskProgress(){
	// ajax 发送请求获取任务运行状态，如果返回运行失败或成功则关闭弹框
	$.ajax({
		type : "POST",
		url : "Monitor",
//			dataType : "json",
		async:false,// 同步执行
		success : function(data) {
			console.info("success:"+data);
//			$("#progressId").progressbar({value: parseInt(data)});
//			$("#progressId").css("width",data+"%");
//			$("#progressId").html(data+"%");
			setProgress("progressId", data+"%");
			if(parseInt(data)-100>=0){
				clearTimeout(t);// 关闭计时器
				// 关闭弹窗进度条
				$('#myModal1').modal("hide");
				console.info("closed!");
				return ;
			}
			t=setTimeout("queryTaskProgress()",100);
		},
		error: function(data){
			console.info("error"+data);
			
		}
	});
}


$(function() {

	// 请求任务进度计时器
	var t ;
	
	// 绑定建模button
	$("#train_model").click(
			function() {
				var input = $('#input_id').val();
				var train_percent = $('#train_percent').val();
				var ranks = $('#ranks').val();

				var lambda = $('#lambda').val();

				var iterations = $('#iterations').val();
				// 发送 ajax请求，调用程序运行
				// 改程序运行为线程模式，直接返回
				var ret = false;
				$.ajax({
					type : "POST",
					url : "RunALS",
					async:false,// 同步执行
					data : {input:input,trainPercent:train_percent,ranks:ranks,lambda:lambda,iterations:iterations},
//					dataType : "json",
					success : function(data) {
						console.info("success:"+data);
						ret = data=="true"?true:false;
					},
					error: function(data){
						console.info("error"+data);
						ret =data=="true"?true:false ;
					}
				});
				// 调用失败
				if(!ret) return ;
				// 弹出窗提示程序正在运行
				setProgress("progressId", "0%");
				$('#myModal1').on('show.bs.modal', function(){
			          var $this = $(this);
			          var $modal_dialog = $this.find('.modal-dialog');
			          // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
			          $this.css('display', 'block');
			          $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
			     });
				$('#myModal1').modal({backdrop: 'static', keyboard: false});
				
				// 定时请求任务进度
				t=setTimeout("queryTaskProgress()",1000);

				console.info("clicked:" + input + "," + train_percent);
			});
	
	
	
});