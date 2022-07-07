    $(async function () {
        tableWithUsers()
        tableWithOneUser()
        createUser()
        createUserForm()
        getDefaultModal()
    })

    const userFetchService = {
        head: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Referer': null
        },

        findAllUsers: async () => await fetch('api/users'),
        getAuthorizedUser: async () => await fetch('api/user'),
        createUser: async (user) => await fetch('api/new', {
            method: 'POST',
            headers: userFetchService.head,
            body: JSON.stringify(user)


        }),
        getOneUser: async (id) => await fetch(`api/${id}`),
        deleteUser: async (id) => await fetch(`api/${id}`, {
            method: 'DELETE',
            headers: userFetchService.head
        }),
        editUser: async (user) => await fetch('api/edit', {
            method: 'PUT',
            headers: userFetchService.head,
            body: JSON.stringify(user)
        })
    }

    // массив ролей
    const roleJson = []

    fetch('api/roles')
        .then(res => res.json())
        .then(roles => roles.forEach(role => roleJson.push(role)))

    //таблица пользователя
    async function tableWithOneUser() {
        let table = $('#tableWithOneUser');
        table.empty();

        await userFetchService.getAuthorizedUser()
            .then(res => res.json())
            .then(user => {
                let tablePrincipal = `$(
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.surname}</td>
                            <td>${user.email}</td>
                            <td>${user.age}</td>
                            <td>${user.roles.map(role => role.name)}</td>
                        </tr>            
                    )`
                table.append(tablePrincipal)
            })
    }

    // таблица с пользователями
    async function tableWithUsers() {
        const usersList = document.querySelector('#tableWithUsers')
        let result = ''

        await userFetchService.findAllUsers()
            .then(res => res.json())
            .then(users => users.forEach(user => {
                result += `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.surname}</td>
                            <td>${user.email}</td>
                            <td>${user.age}</td>
                            <td>${user.roles.map(role => role.name)}</td>
                            <td>
                                <button type="button" data-userid="${user.id}" data-action="edit" class="btn btn-info"
                                data-toggle="modal" data-target="#someDefaultModal">Edit</button>
                            </td>
                            <td>
                                <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-danger"
                                data-toggle="modal" data-target="#someDefaultModal">Delete</button>
                            </td>
                        </tr>
                    `
            }))
        usersList.innerHTML = result

        $("#tableWithUsers").find('button').on('click', (event) => {
            let defaultModal = $('#someDefaultModal');

            let targetButton = $(event.target);
            let buttonUserId = targetButton.attr('data-userid');
            let buttonAction = targetButton.attr('data-action');

            defaultModal.attr('data-userid', buttonUserId);
            defaultModal.attr('data-action', buttonAction);
            defaultModal.modal('show')
        })
    }




    // создание нового пользователя
    async function createUser() {
        $('#createUserButton').click(async () => {
            let createUserForm = $('#createUserForm')
            let name = createUserForm.find('#newUserName').val().trim();
            let surname = createUserForm.find('#newUserSurname').val().trim();
            let email = createUserForm.find('#newUserEmail').val().trim();
            let age = createUserForm.find('#newUserAge').val().trim();
            let password = createUserForm.find('#newUserPassword').val().trim();
            let rolesArray = createUserForm.find('#newUserRoles').val()
            let roles = []

            for (let r of roleJson) {
                for (let i = 0; i < rolesArray.length; i++) {
                    if (r.id == rolesArray[i]) {
                        roles.push(r)
                    }
                }
            }

            let data = {
                name: name,
                surname: surname,
                email: email,
                age: age,
                password: password,
                roles: roles
            }
            const response = await userFetchService.createUser(data);
            if (response.ok) {

                tableWithUsers()
            }
        })
    }

    async function createUserForm() {
        let form = $(`#createUserForm`)

        fetch('/api/roles').then(function (response) {
            form.find('#newUserRoles').empty();
            response.json().then(roleList => {
                roleList.forEach(role => {
                    form.find('#newUserRoles')
                        .append($('<option>').val(role.id).text(role.name));
                })
            })
        })
    }


    async function getDefaultModal() {
        $('#someDefaultModal').modal({
            keyboard: true,
            backdrop: "static",
            show: false
        }).on("show.bs.modal", (event) => {
            let thisModal = $(event.target);
            let userid = thisModal.attr('data-userid');
            let action = thisModal.attr('data-action');

            switch (action) {
                case 'edit':
                    editUser(thisModal, userid);
                    break;
                case 'delete':
                    deleteUser(thisModal, userid);
                    break;
            }
        }).on("hidden.bs.modal", (e) => {

            let thisModal = $(e.target);
            thisModal.find('.modal-title').html('');
            thisModal.find('.modal-body').html('');
            thisModal.find('.modal-footer').html('');
        })
    }
    // удаление пользователя
    async function deleteUser(modal, id) {
        let thisUser = await userFetchService.getOneUser(id)
        let user = thisUser.json()
        let modalForm = $(`#someDefaultModal`)
        let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
        let deleteButton = `<button class="btn btn-danger" id="deleteButton" data-dismiss="modal" data-backdrop="false">Delete</button>`

        modal.find('.modal-title').html('Delete user')
        modal.find('.modal-footer').append(closeButton)
        modal.find('.modal-footer').append(deleteButton)


        user.then(user => {
            let bodyForm = `<form id="deleteUser">
                                    <div class="col-md-7 offset-md-3 text-center">
                                        <div class="form-group">
                                            <span class="font-weight-bold">ID</span>
                                            <input type="text" value="${user.id}" name="id" class="form-control" readonly>
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">First name</span>
                                            <input type="text" value="${user.name}" name="name" class="form-control" readonly>
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Last name</span>
                                            <input type="text" value="${user.surname}" name="surname" class="form-control" readonly>
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Email</span>
                                            <input type="email" value="${user.email}" name="email" class="form-control" readonly>
                                        </div>                                    
                                        <div class="form-group">
                                            <span class="font-weight-bold">Age</span>
                                            <input type="text" value="${user.age}" name="age" class="form-control" readonly>
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Role</span>
                                            <select multiple class="form-control" id="deleteRoles" size="2" readonly>`

            modal.find('.modal-body').append(bodyForm)
        })

        fetch('/api/roles').then(function (response) {
            modalForm.find('#deleteRoles').empty();
            response.json().then(roleList => {
                roleList.forEach(role => {
                    modalForm.find('#deleteRoles')
                        .append($('<option>').val(role.id).text(role.name));
                })
            })
        })

        $(`#deleteButton`).on('click', async () => {
            const response = await userFetchService.deleteUser(id);
            if (response.ok) {
                tableWithUsers()
                modal.modal('hide');
            }
        })
    }

    // редактирование пользователя
    async function editUser(modal, id) {
        let thisUser = await userFetchService.getOneUser(id)
        let user = thisUser.json()
        let modalForm = $(`#someDefaultModal`)

        let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
        let deleteButton = `<button class="btn btn-primary" id="editButton" data-dismiss="modal" data-backdrop="false">Edit</button>`

        modal.find('.modal-title').html('Edit user')
        modal.find('.modal-footer').append(closeButton)
        modal.find('.modal-footer').append(deleteButton)

        user.then(user => {
            let bodyForm = `<form id="editUser">
                                    <div class="col-md-7 offset-md-3 text-center">
                                        <div class="form-group">
                                            <span class="font-weight-bold">ID</span>
                                            <input type="text" value="${user.id}" name="id" id="id" class="form-control" readonly>
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">First name</span>
                                            <input type="text" value="${user.name}" name="name" id="name" class="form-control">
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Last name</span>
                                            <input type="text" value="${user.surname}" name="surname" id="surname" class="form-control">
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Email</span>
                                            <input type="email" value="${user.email}" name="email" id="email" class="form-control">
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Email</span>
                                            <input type="text" value="${user.age}" name="age" id="age" class="form-control">
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Password</span>
                                            <input type="password" value="${user.password}" name="password" id="password" class="form-control">
                                        </div>
                                        <div class="form-group">
                                            <span class="font-weight-bold">Role</span>
                                            <select multiple class="form-control" id="editRoles" size="2">`
            modal.find('.modal-body').append(bodyForm)
        })

        fetch('/api/roles').then(function (response) {
            modalForm.find('#editRoles').empty();
            response.json().then(roleList => {
                roleList.forEach(role => {
                    modalForm.find('#editRoles')
                        .append($('<option>').val(role.id).text(role.name));
                })
            })
        })

        $("#editButton").on('click', async () => {
            let id = modal.find('#id').val()
            let name = modal.find('#name').val()
            let surname = modal.find('#surname').val()
            let email = modal.find('#email').val()
            let age = modal.find('#age').val()
            let password = modal.find('#password').val()
            let rolesArray = modal.find('#editRoles').val()
            let roles = []

            for (let r of roleJson) {
                for (let i = 0; i < rolesArray.length; i++) {
                    if (r.id == rolesArray[i]) {
                        roles.push(r)
                    }
                }
            }

            let data = {
                id: id,
                name: name,
                surname: surname,
                email: email,
                age: age,
                password: password,
                roles: roles
            }

            const response = await userFetchService.editUser(data)
            if (response.ok) {
                await tableWithUsers()
                modal.modal('hide')
            }
        })


    }
