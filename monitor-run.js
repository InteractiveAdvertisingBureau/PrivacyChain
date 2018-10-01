var mailer = require('./monitor-mailer.js');
var http = require("http");
var YAML = require('yamljs');
var os = require('os');
var fs = require('fs');

const monitor_config = YAML.load('monitor.yaml');
let run_mode = 'check';
if(process.argv.length > 2){
  run_mode = process.argv[2];
};
const d = new Date().toLocaleString().split(' ')[0];

let summary_subject = monitor_config.mail.subject.summary + ' of ' + d + ' from ' + os.hostname();
let summary_file = monitor_config.email_content_file;
let summary_invoke_key = monitor_config.mail.invoke_summary_key
let summary_tmp_subject = '';
let summary_tmp_body = '';
http.get(monitor_config.test_url, (resp) => {
  let data = '';

  // A chunk of data has been recieved.
  resp.on('data', (chunk) => {
    data += chunk;
  });

  // The whole response has been received. Print out the result.
  resp.on('end', () => {
    let send_mail_flag = false;
    let email_subject = '';
    let email_body = '';
    console.log(data);
    if(resp.statusCode == 200){
    	let data_json = JSON.parse(data);
    	let returnCode = data_json.returnCode
    	console.log(returnCode)
    	if(returnCode == '000' || returnCode == '105'){
    		email_subject = monitor_config.mail.subject.good;
    		email_body = 'At least ' + monitor_config.test_url + ' works fine with below json:\n\n' + JSON.stringify(data_json, null, 2) + '\n';
    	}else{
    		email_subject = monitor_config.mail.subject.doubtable;
    		email_body = 'Api server is working but ' + monitor_config.test_url + ' retreives no data with below json:\n\n' + JSON.stringify(data_json, null, 2) + '\n';
        send_mail_flag = true;
      }
    }else{
      email_subject = monitor_config.mail.subject.bad;
      email_body = 'Api server is working but ' + monitor_config.test_url + ' get wrong status code ' + resp.statusCode + ' with below response\n\n' + JSON.stringify(JSON.parse(data), null, 2) + '\n';
      send_mail_flag = true;
    }
    // send mail if true
  	if(send_mail_flag){
      mailer({
    	 subject: email_subject + ' from ' + os.hostname(),
    	 text: email_body
      });
    }
    // collect summary
    summary_tmp_subject = email_subject;
    summary_tmp_body = email_body;

    // deal with summary
    summary();
  });

}).on("error", (err) => {
  console.log("Error: " + err.message);
  summary_tmp_subject = monitor_config.mail.subject.error + ' from ' + os.hostname();
  summary_tmp_body = 'Error: ' + err.message + '\n\nThis means that apis server is not running as expected.\n';
  mailer({
    	subject: summary_tmp_subject,
    	text: summary_tmp_body
    });
  // deal with summary
  summary();
});

function summary(){
  //append summary to file and send email if needed
  fs.appendFile(summary_file, new Date().toLocaleString() + '\n' + summary_tmp_subject + '\n' + summary_tmp_body + '\n', (err) => {
    if (err) throw err;
    console.log('The "data to append" was appended to file [' + summary_file + ']!');
  });
  console.log('run mode: '+ run_mode);
  if(run_mode == summary_invoke_key){
    fs.readFile(summary_file, (err, data) => {
      if (err) throw err;
      mailer({
        subject: summary_subject,
        text: data
      });
    });
    // delete summary_file
    fs.unlink(summary_file, (err) => {
      if (err) throw err;
      console.log('unlink file [' + summary_file + ' succeeded.');
    });
  }
};
