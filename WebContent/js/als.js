$(function() {

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
				$.ajax({
					type : "POST",
					url : "Test",
					data : {input:input,trainPercent:train_percent,ranks:ranks,lambda:lambda,iterations:iterations},
//					dataType : "json",
					success : function(data) {
						console.info("success:"+data);
					},
					error: function(data){
						console.info("error"+data);
					}
				});
				
				// 弹出窗提示程序正在运行
				
				// ajax 发送请求获取任务运行状态，如果返回运行失败或成功则关闭弹框
				$.ajax({
					type : "POST",
					url : "Monitor",
//					dataType : "json",
					success : function(data) {
						console.info("success:"+data);
						
					},
					error: function(data){
						console.info("error"+data);
					}
				});

				console.info("clicked:" + input + "," + train_percent);
			});
});