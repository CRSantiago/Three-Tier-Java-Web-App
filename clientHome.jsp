<% String returnMessage = (String) session.getAttribute("returnMessage");
if(returnMessage == null){returnMessage="";} String table = (String)
session.getAttribute("table"); if(table == null){table="";} String sqlStatement
= (String) request.getParameter("sqlStatement"); if(sqlStatement ==
null){sqlStatement="empty";}%>

<html>
  <head>
    <style>
      body {
        background-color: #f8f7ff;
      }
      .content {
        position: absolute;
        left: 50%;
        top: 50%;
        -webkit-transform: translate(-50%, -50%);
        transform: translate(-50%, -50%);
        width: 90%;
        height: 90%;
        background-color: #ffeedd;
        display: flex;
        flex-direction: column;
        align-items: center;
        font-family: 'Roboto', sans-serif;
        box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
        border-radius: 25px;
        overflow-y: scroll;
      }
      .top-div {
        display: flex;
        align-items: center;
        flex-direction: column;
        height: 20%;
        padding-top: 1.5em;
        width: 100%;
      }
      .user-text {
        color: red;
      }
      hr.break {
        display: block;
        background-color: #323234;
        height: 4px;
        width: 95%;
        margin-top: 2.5em;
      }
      .text-div {
        text-align: center;
      }
      .form-div {
        width: 100%;
        height: 40%;
        text-align: center;
        margin-bottom: 1.8em;
      }
      .form-div p {
        margin-top: 1.8em;
      }
      form {
        width: 100%;
        height: 100%;
        /* background-color: #323234; */
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
      }
      textarea {
        width: 80%;
        height: 90%;
        resize: none;
        font-size: 18px;
      }
      .form-buttons {
        padding-top: 0.5em;
        display: flex;
        width: 60%;
        justify-content: space-around;
      }
      .executeBtn {
        border: none;
        color: #1ed760;
        padding: 8px 16px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 14px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        background-color: #111111;
        border: 2px solid #555555;
      }

      .executeBtn:hover {
        background-color: #555555;
        color: white;
      }
      .resetBtn {
        border: none;
        color: #ff9be7;
        padding: 8px 16px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 14px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        background-color: #111111;
        border: 2px solid #555555;
      }

      .resetBtn:hover {
        background-color: #555555;
        color: white;
      }
      .clearBtn {
        border: none;
        color: #eeba0b;
        padding: 8px 16px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 14px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        background-color: #111111;
        border: 2px solid #555555;
      }

      .clearBtn:hover {
        background-color: #555555;
        color: white;
      }

      table,
      th,
      td {
        border: 1px solid;
      }
      table {
        width: 100%;
        margin-bottom: 1.5em;
      }
      th {
        padding: 15px;
      }
      tr {
        height: 50px;
      }
      td {
        text-align: center;
        padding: 15px;
      }
      .sql-error-message {
        display: flex;
        justify-content: center;
        flex-direction: column;
        background-color: crimson;
        padding: 10px;
        color: white;
        text-align: center;
        border: 3px solid black;
      }

      .sql-error-message p {
        margin-top: 0px;
      }
    </style>
  </head>
  <body>
    <div class="content">
      <div class="top-div">
        <h1>CNT 4714 Enterprise Database System</h1>
        <h3>
          A Servlet/JPS-based Multi-tiered Enterprise Application Using a Tomcat
          Container
        </h3>
        <hr class="break" />
      </div>
      <div class="text-div">
        <p>
          You are connected to the Project 4 Enterprise System database as a
          <span class="user-text">client-level</span> user.
        </p>
        <p>
          Please enter any valid SQL query or update command in the box below.
        </p>
      </div>
      <div class="form-div">
        <form action="/CNT4714-Project4/clientuser" method="POST">
          <textarea rows="50" cols="150" name="sqlStatement">
<%=sqlStatement%></textarea
          >
          <div class="form-buttons">
            <button
              class="executeBtn"
              type="submit"
              name="action"
              value="execute"
            >
              Execute Command
            </button>
            <button class="resetBtn" type="submit" name="action" value="reset">
              Reset Form
            </button>
            <button class="clearBtn" type="submit" name="action" value="clear">
              Clear Results
            </button>
          </div>
        </form>
        <p>All execution results will appear below this line.</p>
      </div>
      <hr class="break" />
      <div>
        <p><strong>Database Results:</strong></p>

        <div <% if (!returnMessage.isEmpty()) { %>
          class="sql-error-message"<%} else {%>style="display:none" <%}%>>
          <h4>Error in executing SQL statement:</h4>
          <p><%=returnMessage%></p>
        </div>
        <div id="table"><%=table%></div>
      </div>
    </div>
  </body>
</html>
