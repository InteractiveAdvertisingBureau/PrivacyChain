// file: mailer.js
var _ = require('lodash');  
var nodemailer = require('nodemailer');
var YAML = require('yamljs');

const monitor_config = YAML.load('monitor.yaml');

var config = {
    host: monitor_config.smtp.host,
    port: monitor_config.smtp.port,
    tls: {
        rejectUnauthorized: false
    }
    //auth: {
        //user: 'xxx@xxx.com',
        //pass: '你的密码'
    //}
};
    
var transporter = nodemailer.createTransport(config);

var defaultMail = {
    from: monitor_config.mail.from,
    to: monitor_config.mail.to,
    cc: monitor_config.mail.cc
};

module.exports = function(mail){
    // 应用默认配置
    mail = _.merge({}, defaultMail, mail);
    
    // 发送邮件
    transporter.sendMail(mail, function(error, info){
        if(error) return console.log(error);
        console.log('mail sent:', info.response);
    });
};
